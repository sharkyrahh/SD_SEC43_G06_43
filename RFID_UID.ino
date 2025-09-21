//Includes
#include <MFRC522v2.h>
#include <MFRC522DriverSPI.h>
#include <MFRC522DriverPinSimple.h>
#include <MFRC522Debug.h>
#include <LiquidCrystal_I2C.h>
#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"
#include <NTPClient.h>
#include <WiFiUdp.h>

//Defines
#define WIFI_SSID "kira"
#define WIFI_PASSWORD "54145414"
#define API_KEY "AIzaSyA2H51yaBq0Gt2UmnmZhGiSarJz0DU5LJo"
#define DATABASE_URL "https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/"
#define SS_PIN 5
#define RST_PIN 2
#define TIME_OFFSET 28800
LiquidCrystal_I2C lcd(0x27, 16, 2);
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

//global variables
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

//Setup() function
void setup() {
  Serial.begin(115200);
  while (!Serial);

 // MFRC522Debug::PCD_DumpVersionToSerial(mfrc522, Serial); -- previous code, kept in case nak reuse

 // LCD start up message
  lcd.init();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Initializing...");

  // initialization, shows in serial monitor
  // rfid
  mfrc522.PCD_Init();
  Serial.println("RFID Reader Initialized");

  // wifi
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

  // firebase
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

  // time ?? for timestamp
  timeClient.begin();
  timeClient.setTimeOffset(28800);  
  timeClient.setUpdateInterval(60000);

  // to switch display states
  unsigned long currentMillis = millis();
    
  if (currentMillis - previousDisplayTime >= displayInterval) {
    previousDisplayTime = currentMillis;
  }

  if (displayState == 0) {
        // parking availability message
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("Avail. Parking:");
        lcd.setCursor(0, 1);
        // need to read from firebase parking slot later. right now just dummy data
        lcd.print("9");
        delay(2000);
        displayState = 1;
      } else {
        // tap card message
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("Please tap your");
        lcd.setCursor(0, 1);
        lcd.print("card.");
        delay(2000);
        displayState = 0;
      }
}

// time function for timestamp
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
  
  // Convert epoch time to date
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
    randomName += char(random(65, 91)); // A-Z
  }
  return randomName;
}

// get registerMode bool from database
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

// loop function
void loop() {
  // in case wifi disconnected
  if (!wifiConnected) {
    if (WiFi.status() == WL_CONNECTED) {
      wifiConnected = true;
      Serial.println("\nReconnected to WiFi");
    }
    delay(1000);
    return;
  }

  // read registerMode
   static unsigned long lastReadTime = 0;
  if (millis() - lastReadTime > 1000) {
    readRegisterMode();
    lastReadTime = millis();
  }

  // initialize rfid i think
  static unsigned long lastReset = 0;
  if (millis() - lastReset > 5000) {
    mfrc522.PCD_Init();
    lastReset = millis();
  }

  // registerMode code
  if(registerMode){
    if (!mfrc522.PICC_IsNewCardPresent() || !mfrc522.PICC_ReadCardSerial()) {

      unsigned long currentMillis = millis();
      if (displayState == 0) {
      if (currentMillis - previousDisplayTime >= 2000) { // Show parking for 2 seconds
        previousDisplayTime = currentMillis;
        lcd.clear();
         lcd.setCursor(0, 0);
          lcd.print("Register RFID");
          lcd.setCursor(0, 1);
          lcd.print("Card");
        displayState = 1;
      }
    } else {
      if (currentMillis - previousDisplayTime >= 2000) { // Show tap card for 2 seconds
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


  // normal mode code
  else{
  // Only proceed if new card
  if (!mfrc522.PICC_IsNewCardPresent() || !mfrc522.PICC_ReadCardSerial()) {

  unsigned long currentMillis = millis();

  if (displayState == 0) {
      if (currentMillis - previousDisplayTime >= 2000) { // Show parking for 2 seconds
        previousDisplayTime = currentMillis;
        lcd.clear();
          lcd.setCursor(0, 0);
          lcd.print("Avail. Parking:");
          lcd.setCursor(0, 1);
          lcd.print("9");
        displayState = 1;
      }
    } else {
      if (currentMillis - previousDisplayTime >= 2000) { // Show tap card for 2 seconds
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

  // Read UID
  String uidString = "";
  for (byte i = 0; i < mfrc522.uid.size; i++) {
    if (mfrc522.uid.uidByte[i] < 0x10) uidString += "0";
    uidString += String(mfrc522.uid.uidByte[i], HEX);
  }

  mfrc522.PICC_HaltA();

  Serial.println("Scanned UID: " + uidString);

  // RegisterMode = send UID to database
  if (registerMode){
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Card Scanned:");
    lcd.setCursor(0, 1);
    
    // Display first 16 characters of UID
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
  }

  // Normal Mode = check if card exist
  else {
    if (Firebase.ready() && signupOK) {

    String cardPath = "cards/" + uidString;

    // Not Registered
    if (Firebase.RTDB.get(&fbdo, cardPath.c_str())) {
      if (fbdo.dataType() == "null") {
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("Entry Denied");
      }
      else { // Registered
        String enteredPath = cardPath + "/hasEntered";
        if (Firebase.RTDB.getBool(&fbdo, enteredPath.c_str())){
          bool hasEntered = fbdo.boolData();
          
          // amik UID of user yang dah diregister
          String userUID;
          String UIDpath = cardPath + "/UID";
          if (Firebase.RTDB.getString(&fbdo, UIDpath.c_str())){
            userUID = fbdo.stringData();
          }

          String randomChild = generateRandomChildName();

          // Exit
          if (hasEntered){
            lcd.clear();
            lcd.setCursor(0, 0);
            lcd.print("Exit Success");
            Firebase.RTDB.setBool(&fbdo, enteredPath.c_str(), false);

            Firebase.RTDB.setString(&fbdo, "exitLog/" + randomChild + "/UID", userUID);
            Firebase.RTDB.setString(&fbdo, "exitLog/" + randomChild + "/timestamp", getFormattedTime());
            Firebase.RTDB.setString(&fbdo, "exitLog/" + randomChild + "/day", getDayOfWeek());
            Firebase.RTDB.setString(&fbdo, "exitLog/" + randomChild + "/date", getFormattedDate());
            
          } else {
          // Enter
            lcd.clear();
            lcd.setCursor(0, 0);
            lcd.print("Entry Success");
            Firebase.RTDB.setBool(&fbdo, enteredPath.c_str(), true);

            Firebase.RTDB.setString(&fbdo, "entryLog/" + randomChild + "/UID", userUID);
            Firebase.RTDB.setString(&fbdo, "entryLog/" + randomChild + "/timestamp", getFormattedTime());
            Firebase.RTDB.setString(&fbdo, "entryLog/" + randomChild + "/day", getDayOfWeek());
            Firebase.RTDB.setString(&fbdo, "entryLog/" + randomChild + "/date", getFormattedDate());

          } // Print PlateNumber
            String plateNumPath = cardPath + "/plateNum";
            if (Firebase.RTDB.get(&fbdo, plateNumPath.c_str())){
              String plateNum = fbdo.stringData();
              lcd.setCursor(0, 1);
              lcd.print(plateNum);
           }
            delay(2000);
        } 
      }
    } else { // Not Registered
        Serial.println("Database error or no cards: " + fbdo.errorReason());
          lcd.clear();
          lcd.setCursor(0, 0);
          lcd.print("Entry Denied");
          delay(2000); // Show denied message
          }
  }
  }

  while (mfrc522.PICC_IsNewCardPresent()) {
    delay(100);
  }

  lcd.clear();
  delay(500);
}