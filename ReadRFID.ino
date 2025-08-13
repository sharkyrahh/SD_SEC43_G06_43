#include <MFRC522v2.h>
#include <MFRC522DriverSPI.h>
#include <MFRC522DriverPinSimple.h>
#include <MFRC522Debug.h>

MFRC522DriverPinSimple ss_pin(5);

MFRC522DriverSPI driver{ss_pin}; 
MFRC522 mfrc522{driver};  

void setup() {
  Serial.begin(115200);  
  while (!Serial);      
  
  mfrc522.PCD_Init();   
  MFRC522Debug::PCD_DumpVersionToSerial(mfrc522, Serial);	
  Serial.println(F("Scan PICC to see UID, SAK, type, and data blocks..."));
}

void loop() {
  if (!mfrc522.PICC_IsNewCardPresent()) {
    return;
  }

  if (!mfrc522.PICC_ReadCardSerial()) {
    return;
  }

  MFRC522Debug::PICC_DumpToSerial(mfrc522, Serial, &(mfrc522.uid));

  delay(2000);
}
