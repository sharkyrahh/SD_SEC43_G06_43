#include <MFRC522v2.h>
#include <MFRC522DriverSPI.h>
#include <MFRC522DriverPinSimple.h>
#include <LiquidCrystal_I2C.h>
#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <ESP32Servo.h>

#define WIFI_SSID "kira"
#define WIFI_PASSWORD "54145414"
#define API_KEY "AIzaSyA2H51yaBq0Gt2UmnmZhGiSarJz0DU5LJo"
#define DATABASE_URL "https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/"
#define SS_PIN 5
#define RST_PIN 2
#define TIME_OFFSET 28800
#define BUZZER_PIN 12
LiquidCrystal_I2C lcd(0x27, 16, 2);
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

unsigned long sendDataPrevMills = 0;
bool signupOK = false;
bool wifiConnected = false;
MFRC522DriverPinSimple ss_pin(5);
MFRC522DriverSPI driver{ss_pin};
MFRC522 mfrc522{driver};
int displayState = 0;
unsigned long previousDisplayTime = 0;
const long displayInterval = 2000;
bool registerMode;
int parkingCount;
static const int servoPin = 13;
Servo servo1;

void setup() {
  Serial.begin(115200);

  pinMode(BUZZER_PIN, OUTPUT);
  digitalWrite(BUZZER_PIN, LOW);

  servo1.attach(servoPin);
  servo1.write(90);

  lcd.init();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Initializing...");

  mfrc522.PCD_Init();
  Serial.println("RFID Reader Initialized");

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.println("Connecting to Wi-Fi");

  unsigned long wifiStartTime = millis();
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
    if (millis() - wifiStartTime > 30000) {
      Serial.println("\nFailed to connect to Wi-Fi!");
      break;
    }
  }

  if (WiFi.status() == WL_CONNECTED) {
    wifiConnected = true;
    Serial.println("\nConnected to WiFi");
  }

  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  config.timeout.serverResponse = 120000;

  if(Firebase.signUp(&config, &auth, "", "")){
    Serial.println("signup Ok");
    signupOK = true;
  } else {
    Serial.printf("Signup error: %s\n", config.signer.signupError.message.c_str());
  }

  config.token_status_callback = tokenStatusCallback;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  timeClient.begin();
  timeClient.setTimeOffset(28800);  
  timeClient.setUpdateInterval(60000);

  unsigned long currentMillis = millis();
    
  if (currentMillis - previousDisplayTime >= displayInterval) {
    previousDisplayTime = currentMillis;
  }

  if (displayState == 0) {
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("Avail. Parking:");
        lcd.setCursor(0, 1);

        if (Firebase.RTDB.getInt(&fbdo, "/Parking/parkingCount")) {
        if (fbdo.dataType() == "int") {
        parkingCount = fbdo.intData();
        lcd.print(parkingCount);}}
        
        delay(2000);
        displayState = 1;
      } else {

        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("Please tap your");
        lcd.setCursor(0, 1);
        lcd.print("card.");
        delay(2000);
        displayState = 0;
      }
}

void buzzerEntry() {
  digitalWrite(BUZZER_PIN, HIGH);
  delay(200);
  digitalWrite(BUZZER_PIN, LOW);
  delay(100);
  digitalWrite(BUZZER_PIN, HIGH);
  delay(200);
  digitalWrite(BUZZER_PIN, LOW);
}

void buzzerExit() {
  digitalWrite(BUZZER_PIN, HIGH);
  delay(100);
  digitalWrite(BUZZER_PIN, LOW);
  delay(50);
  digitalWrite(BUZZER_PIN, HIGH);
  delay(100);
  digitalWrite(BUZZER_PIN, LOW);
  delay(50);
  digitalWrite(BUZZER_PIN, HIGH);
  delay(100);
  digitalWrite(BUZZER_PIN, LOW);
}

void buzzerError() {
  digitalWrite(BUZZER_PIN, HIGH);
  delay(800);
  digitalWrite(BUZZER_PIN, LOW);
}

void checkForAppTap() {
  if (Firebase.ready() && signupOK) {
    if (Firebase.RTDB.getString(&fbdo, "/RFID/UID")) {
      String appUid = fbdo.stringData();
      
      if (Firebase.RTDB.getBool(&fbdo, "/RFID/scanActive")) {
        bool scanActive = fbdo.boolData();
        
        if (scanActive && appUid.length() > 0) {
          Serial.println("App tap detected: " + appUid);
          
          processUid(appUid);
          
          Firebase.RTDB.setString(&fbdo, "/RFID/UID", "");
          Firebase.RTDB.setBool(&fbdo, "/RFID/scanActive", false);
        }
      }
    }
  }
}

void processUid(String uidString) {
  Serial.println("Processing UID: " + uidString);

  if (registerMode){
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Card Scanned:");
    lcd.setCursor(0, 1);

    if (uidString.length() > 16) {
      lcd.print(uidString.substring(0, 16));
    } else {
      lcd.print(uidString);
    }

    if (Firebase.ready() && signupOK && WiFi.status() == WL_CONNECTED) {
      Firebase.RTDB.setBool(&fbdo, "RFID/scanActive", true);
      
      if(Firebase.RTDB.setString(&fbdo, "RFID/UID", uidString) && 
         Firebase.RTDB.setString(&fbdo, "RFID/timestamp", getFormattedTime())) {
        
        Serial.println("Data sent to Firebase");
        
        delay(2000);
        
        Firebase.RTDB.setString(&fbdo, "RFID/UID", "");
        Firebase.RTDB.setBool(&fbdo, "RFID/scanActive", false);
      }
    }
  } else {
    processCardAccess(uidString);
  }
}

void processCardAccess(String uidString) {
  if (Firebase.ready() && signupOK) {
    String cardPath = "cards/" + uidString;

    if (Firebase.RTDB.get(&fbdo, cardPath.c_str())) {
      if (fbdo.dataType() == "null") {
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("Entry Denied");
        buzzerError(); 
        delay(2000);
      } else { 
        String enteredPath = cardPath + "/hasEntered";
        if (Firebase.RTDB.getBool(&fbdo, enteredPath.c_str())){
          bool hasEntered = fbdo.boolData();
          
          String userUID;
          String UIDpath = cardPath + "/UID";
          if (Firebase.RTDB.getString(&fbdo, UIDpath.c_str())){
            userUID = fbdo.stringData();
          }

          String plateNum;
          String plateNumPath = cardPath + "/plateNum";
          if (Firebase.RTDB.get(&fbdo, plateNumPath.c_str())){
            plateNum = fbdo.stringData();
          }

          String randomChild = generateRandomChildName();

          if (hasEntered){
            lcd.clear();
            lcd.setCursor(0, 0);
            lcd.print("Exit Success");
            buzzerExit();

            String userSlot = findReservedSlot(userUID);
            if (userSlot != "") {
              Firebase.RTDB.setString(&fbdo, "/Parking/" + userSlot + "/status", "Available");
              Firebase.RTDB.setString(&fbdo, "/Parking/" + userSlot + "/reservedBy", "");
            }

            servo1.write(0);  
            delay(1000); 
            Firebase.RTDB.setBool(&fbdo, enteredPath.c_str(), false);
            parkingCount = parkingCount + 1;
            Firebase.RTDB.setInt(&fbdo, "Parking/parkingCount", parkingCount);

            Firebase.RTDB.setString(&fbdo, "exitLog/" + randomChild + "/UID", userUID);
            Firebase.RTDB.setString(&fbdo, "exitLog/" + randomChild + "/timestamp", getFormattedTime());
            Firebase.RTDB.setString(&fbdo, "exitLog/" + randomChild + "/day", getDayOfWeek());
            Firebase.RTDB.setString(&fbdo, "exitLog/" + randomChild + "/date", getFormattedDate());
            Firebase.RTDB.setString(&fbdo, "exitLog/" + randomChild + "/plateNum", plateNum);

            servo1.write(90); 
            delay(1000); 
            
          } else {
            lcd.clear();
            lcd.setCursor(0, 0);
            lcd.print("Entry Success");
            buzzerEntry();

            String hasReservePath = "users/" + userUID + "/hasReserve";
            if (Firebase.RTDB.getBool(&fbdo, hasReservePath.c_str())) {
              bool hasReserve = fbdo.boolData();
              
              if (hasReserve) {
                String reservedSlot = findReservedSlot(userUID);
                if (reservedSlot != "") {
                  Firebase.RTDB.setString(&fbdo, "/Parking/" + reservedSlot + "/status", "Full");
                  lcd.setCursor(0, 1);
                  lcd.print("Reserved: " + reservedSlot);
                }
              } else {
                String availableSlot = findAvailableSlot();
                if (availableSlot != "") {
                  Firebase.RTDB.setString(&fbdo, "/Parking/" + availableSlot + "/status", "Full");
                  Firebase.RTDB.setString(&fbdo, "/Parking/" + availableSlot + "/reservedBy", userUID);
                } 
              }
            }

            servo1.write(0); 
            delay(1000); 

            Firebase.RTDB.setBool(&fbdo, enteredPath.c_str(), true);
            parkingCount = parkingCount - 1;
            Firebase.RTDB.setInt(&fbdo, "Parking/parkingCount", parkingCount);

            Firebase.RTDB.setString(&fbdo, "entryLog/" + randomChild + "/UID", userUID);
            Firebase.RTDB.setString(&fbdo, "entryLog/" + randomChild + "/timestamp", getFormattedTime());
            Firebase.RTDB.setString(&fbdo, "entryLog/" + randomChild + "/day", getDayOfWeek());
            Firebase.RTDB.setString(&fbdo, "entryLog/" + randomChild + "/date", getFormattedDate());
            Firebase.RTDB.setString(&fbdo, "entryLog/" + randomChild + "/plateNum", plateNum);

            servo1.write(90); 
            delay(1000); 
          } 
              
          lcd.setCursor(0, 1);
          lcd.print(plateNum);
          delay(2000);
        } 
      }
    } else { 
      Serial.println("Database error or no cards: " + fbdo.errorReason());
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Entry Denied");
      buzzerError(); 
      delay(2000); 
    }
  }
}

String getFormattedTime() {
  timeClient.update();
  
  int hours = timeClient.getHours();
  int minutes = timeClient.getMinutes();
  
  static char timeString[6];
  sprintf(timeString, "%02d:%02d", hours, minutes);

  return String(timeString);
}

String getDayOfWeek() {
  timeClient.update();
  unsigned long epochTime = timeClient.getEpochTime() - TIME_OFFSET;
  time_t rawTime = (time_t)epochTime;
  struct tm *timeInfo = localtime(&rawTime);
  
  char* days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
  return String(days[timeInfo->tm_wday]);
}

String getFormattedDate() {
  timeClient.update();
  
  unsigned long epochTime = timeClient.getEpochTime() - TIME_OFFSET;
  
  time_t rawTime = (time_t)epochTime;
  struct tm *timeInfo = localtime(&rawTime);
  
  static char dateString[11];
  sprintf(dateString, "%04d-%02d-%02d", 
           timeInfo->tm_year + 1900, 
          timeInfo->tm_mon + 1, 
          timeInfo->tm_mday);
  
  return String(dateString);
}

String generateRandomChildName() {
  String randomName = "";
  for (int i = 0; i < 16; i++) {
    randomName += char(random(65, 91)); 
  }
  return randomName;
}

void readRegisterMode() {
  if (Firebase.ready() && signupOK) {
    if (Firebase.RTDB.getBool(&fbdo, "/RFID/registerMode")) {
      if (fbdo.dataType() == "boolean") {
        registerMode = fbdo.boolData();
      }
    } else {
      Serial.println("Failed to read register mode: " + fbdo.errorReason());
    }
  }
}

String findReservedSlot(String userUID) {
  if (Firebase.ready() && signupOK) {
    if (Firebase.RTDB.getJSON(&fbdo, "/Parking")) {
      FirebaseJson *json = fbdo.jsonObjectPtr();
      
      size_t count = json->iteratorBegin();
      for (size_t i = 0; i < count; i++) {
        int type;
        String key;
        String value;
        
        if (json->iteratorGet(i, type, key, value) == 0) {

          if (type == FirebaseJson::JSON_OBJECT) {
            String reservedPath = "/Parking/" + key + "/reservedBy";
            if (Firebase.RTDB.getString(&fbdo, reservedPath.c_str())) {
              if (fbdo.stringData() == userUID) {
                json->iteratorEnd();
                return key; 
              }
            }
          }
        }
      }
      json->iteratorEnd();
    }
  }
  return ""; 
}

String findAvailableSlot() {
  if (Firebase.ready() && signupOK) {
    if (Firebase.RTDB.getJSON(&fbdo, "/Parking")) {
      FirebaseJson *json = fbdo.jsonObjectPtr();
      
      size_t count = json->iteratorBegin();
      for (size_t i = 0; i < count; i++) {
        int type;
        String key;
        String value;
        
        if (json->iteratorGet(i, type, key, value) == 0) {
          if (type == FirebaseJson::JSON_OBJECT) {
            String statusPath = "/Parking/" + key + "/status";
            if (Firebase.RTDB.getString(&fbdo, statusPath.c_str())) {
              if (fbdo.stringData() == "available") {
                json->iteratorEnd();
                return key; 
              }
            }
          }
        }
      }
      json->iteratorEnd();
    }
  }
  return "";
}

void loop() {
  if (!wifiConnected) {
    if (WiFi.status() == WL_CONNECTED) {
      wifiConnected = true;
      Serial.println("\nReconnected to WiFi");
    }
    delay(1000);
    return;
  }

  static unsigned long lastReadTime = 0;
  if (millis() - lastReadTime > 1000) {
    readRegisterMode();
    lastReadTime = millis();
  }

  static unsigned long lastReset = 0;
  if (millis() - lastReset > 5000) {
    mfrc522.PCD_Init();
  lastReset = millis();
  }

  checkForAppTap();
   
 if(registerMode){
    if (!mfrc522.PICC_IsNewCardPresent() || !mfrc522.PICC_ReadCardSerial()) {
      unsigned long currentMillis = millis();
      if (displayState == 0) {
        if (currentMillis - previousDisplayTime >= 2000) { 
          previousDisplayTime = currentMillis;
          lcd.clear();
          lcd.setCursor(0, 0);
          lcd.print("Register RFID");
          lcd.setCursor(0, 1);
          lcd.print("Card");
          displayState = 1;
        }
      } else {
        if (currentMillis - previousDisplayTime >= 2000) { 
          previousDisplayTime = currentMillis;
          lcd.clear();
          lcd.setCursor(0, 0);
          lcd.print("Please tap your");
          lcd.setCursor(0, 1);
          lcd.print("card.");
          displayState = 0;
        }
      }
      return;
    }
  } else {
    if (!mfrc522.PICC_IsNewCardPresent() || !mfrc522.PICC_ReadCardSerial()) {
      unsigned long currentMillis = millis();
      if (displayState == 0) {
        if (currentMillis - previousDisplayTime >= 2000) { 
          previousDisplayTime = currentMillis;
          lcd.clear();
          lcd.setCursor(0, 0);
          lcd.print("Avail. Parking:");
          lcd.setCursor(0, 1);
          if (Firebase.RTDB.getInt(&fbdo, "/Parking/parkingCount")) {
            if (fbdo.dataType() == "int") {
              parkingCount = fbdo.intData();
              lcd.print(parkingCount);
            }
          }
          displayState = 1;
        }
      } else {
        if (currentMillis - previousDisplayTime >= 2000) { 
          previousDisplayTime = currentMillis;
          lcd.clear();
          lcd.setCursor(0, 0);
          lcd.print("Please tap your");
          lcd.setCursor(0, 1);
          lcd.print("card.");
          displayState = 0;
        }
      }
      return;
    }
  }

  String uidString = "";
  for (byte i = 0; i < mfrc522.uid.size; i++) {
    if (mfrc522.uid.uidByte[i] < 0x10) uidString += "0";
    uidString += String(mfrc522.uid.uidByte[i], HEX);
  }

  mfrc522.PICC_HaltA();
  Serial.println("Scanned UID: " + uidString);

  if (registerMode){
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Card Scanned:");
    lcd.setCursor(0, 1);

    if (uidString.length() > 16) {
      lcd.print(uidString.substring(0, 16));
    } else {
      lcd.print(uidString);
    }

    if (Firebase.ready() && signupOK && WiFi.status() == WL_CONNECTED) {
      Firebase.RTDB.setBool(&fbdo, "RFID/scanActive", true);
      
      if(Firebase.RTDB.setString(&fbdo, "RFID/UID", uidString) && 
         Firebase.RTDB.setString(&fbdo, "RFID/timestamp", getFormattedTime())) {
        
        Serial.println("Data sent to Firebase");
        
        delay(2000);
        
        Firebase.RTDB.setString(&fbdo, "RFID/UID", "");
        Firebase.RTDB.setBool(&fbdo, "RFID/scanActive", false);
      }
    }
  } else {
    processCardAccess(uidString);
  }

  while (mfrc522.PICC_IsNewCardPresent()) {
    delay(100);
  }
  lcd.clear();
  delay(500);
}