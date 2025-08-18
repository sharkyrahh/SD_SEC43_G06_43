#include <MFRC522v2.h>
#include <MFRC522DriverSPI.h>
#include <MFRC522DriverPinSimple.h>
#include <MFRC522Debug.h>

#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

#include <NTPClient.h>
#include <WiFiUdp.h>

#define WIFI_SSID "kira"
#define WIFI_PASSWORD "54145414"
#define API_KEY "AIzaSyA2H51yaBq0Gt2UmnmZhGiSarJz0DU5LJo"
#define DATABASE_URL "https://utm-smartparking-system-default-rtdb.asia-southeast1.firebasedatabase.app/"

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

void setup() {
  Serial.begin(115200);
  while (!Serial);

  mfrc522.PCD_Init(); // Initialize RFID reader
  MFRC522Debug::PCD_DumpVersionToSerial(mfrc522, Serial);
  Serial.println(F("Scan PICC to see UID"));

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");

  unsigned long wifiStartTime = millis();
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
    // Timeout after 10 seconds
    if (millis() - wifiStartTime > 10000) {
      Serial.println("\nFailed to connect to Wi-Fi!");
      break;
    }
  }

  if (WiFi.status() == WL_CONNECTED) {
    wifiConnected = true;
    Serial.println("\nConnected to WiFi");
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());
  }

  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  if(Firebase.signUp(&config, &auth, "", "")){
    Serial.println("signup Ok");
    signupOK = true;
  } else {
    Serial.printf("%s\n, config.signer.signupError.message.c_str()");
  }

  config.token_status_callback = tokenStatusCallback;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  timeClient.begin();
  timeClient.setTimeOffset(0);  // UTC time
}

String getFormattedTime() {
  timeClient.update();
  unsigned long epochTime = timeClient.getEpochTime();
  
  // Convert to human-readable format
  struct tm *ptm = gmtime((time_t *)&epochTime);
  
  char timeString[25];
  sprintf(timeString, "%04d-%02d-%02d %02d:%02d:%02d",
          ptm->tm_year + 1900, ptm->tm_mon + 1, ptm->tm_mday,
          ptm->tm_hour, ptm->tm_min, ptm->tm_sec);
          
  return String(timeString);
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

  // Only proceed if new card
  if (!mfrc522.PICC_IsNewCardPresent() || !mfrc522.PICC_ReadCardSerial()) {
    delay(50);
    return;
  }

  // Read UID
  String uidString = "";
  for (byte i = 0; i < mfrc522.uid.size; i++) {
    if (mfrc522.uid.uidByte[i] < 0x10) uidString += "0";
    uidString += String(mfrc522.uid.uidByte[i], HEX);
  }
  
  // Get timestamp
  String timestamp = getFormattedTime();
  mfrc522.PICC_HaltA();

  Serial.println("Scanned UID: " + uidString);

  if (Firebase.ready() && signupOK) {
    Firebase.RTDB.setBool(&fbdo, "RFID/scanActive", true);
    
    if(Firebase.RTDB.setString(&fbdo, "RFID/UID", uidString) && 
       Firebase.RTDB.setString(&fbdo, "RFID/timestamp", timestamp)) {
      
      Serial.println("Data sent to Firebase");
      
      delay(2000);
      
      Firebase.RTDB.setString(&fbdo, "RFID/UID", "");
      Firebase.RTDB.setBool(&fbdo, "RFID/scanActive", false);
    }
  }

  while (mfrc522.PICC_IsNewCardPresent()) {
    delay(100);
  }
  delay(500);
}