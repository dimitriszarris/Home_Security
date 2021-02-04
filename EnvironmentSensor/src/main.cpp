/*
    Based on Neil Kolban example for IDF: https://github.com/nkolban/esp32-snippets/blob/master/cpp_utils/tests/BLE%20Tests/SampleWrite.cpp
    Ported to Arduino ESP32 by Evandro Copercini
*/

#include <Arduino.h>
#include <stdio.h>
#include <esp_wifi.h>
#include "WiFi.h"
#include <WebServer.h>
#include "driver/adc.h"
#include "esp_sleep.h"
#include "rom/rtc.h"
#include "driver/rtc_io.h"
#include <ArduinoJson.h>
#include <Adafruit_BME280.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include "D:\Development\PersonalProjects\Home_Security\Include\ESP32\HttpClientCommands.h"
#include "D:\Development\PersonalProjects\Home_Security\Include\WifiConfiguration.h"
#include "D:\Development\PersonalProjects\Home_Security\Include\HomeServerSettings.h"
#include <EEPROM.h>
#include <Adafruit_BME280.h>


#define _DEBUG

// See the following for generating UUIDs:
// https://www.uuidgenerator.net/

#define SEALEVELPRESSURE_HPA (1013.25)

Adafruit_BME280 bme;

const std::string c_service_uuid = "2714f485-9888-46b8-a119-98586cb867a3";
const std::string c_alarm_characteristic_uuid = "ea3e2887-c7a9-44d6-ba91-f6ddaec2a526";
const std::string c_device_characteristic_uuid = "cc88b0f2-e905-472c-86a7-8381d137d3d8";
const std::string c_wifi_ssid_characteristic_uuid = "81015940-d7a3-44a9-b5b1-4cd1cea1ddf2";
const std::string c_wifi_password_characteristic_uuid = "47578d1f-be08-400a-b2ad-1abd7e1cd9b7";

const int c_device_id_addr = 0;
uint8_t g_device_id = 0; // size 1
const int c_alarm_id_addr = 2;
uint8_t g_alarm_id = 0; // size 1

const int c_wifi_ssid_addr = 4;
std::string g_wifi_ssid = ""; // size 50
const int c_wifi_password_addr = 56;
std::string g_wifi_password = ""; // size 20

//REST server settings
const int c_restServerPort = 8080;

const int c_gasPin = A0;
const int c_securityAlarmPin = 12;
const int c_gasFireAlarmPin = 13;

uint8_t g_securityAlarmOn = 0;
uint8_t g_gasFireAlarmOn = 0;

//Rest Server Resources
const String c_resSensorValues = "/environment_sensor_state";
const char *c_resDevice = "/environment_sensor";

WebServer g_webServer(c_restServerPort);

enum bt_state_ {
  STATE_BT_INITIALIZED,
  STATE_BT_CONNECTED,
  STATE_BT_DISCONNECTED,
};

enum wifi_state_ {
  STATE_WIFI_INITIALIZED,
  STATE_WIFI_CONNECTED,
  STATE_WIFI_DISCONNECTED
};

bt_state_ g_bt_state;
wifi_state_ g_wifi_state;

unsigned long g_lastPostMillis = 0;
const unsigned int c_period = 1 * 1000; // check values every 10 sec
bool g_firstRun = true;

float g_lastTemperatureVal;
float g_lastHumidityVal;
float g_lastPressureVal;
float g_lastAltitudeVal;
int g_lastGasVal;

int initWifi()
{
  int retries = 0;

  WiFi.mode(WIFI_STA);
  WiFi.begin(g_wifi_ssid.c_str(), g_wifi_password.c_str());
  // check the status of WiFi connection to be WL_CONNECTED
  while ((WiFi.status() != WL_CONNECTED) && (retries < MAX_WIFI_INIT_RETRY))
  {
    retries++;
    delay(WIFI_RETRY_DELAY_MS);
  }

  return WiFi.status(); // return the WiFi connection status
}

void onEspRestart()
{
  ESP.restart();
}

void wifiStartup() {
  if (initWifi() == WL_CONNECTED)
  {
    g_wifi_state = wifi_state_::STATE_WIFI_CONNECTED;
    #ifdef _DEBUG
    Serial.print("Connected to ");
    Serial.print(g_wifi_ssid.c_str());
    Serial.print("--- IP: ");
    Serial.println(WiFi.localIP());
    #endif
  }
  else
  {
    #ifdef _DEBUG
    Serial.print("Error connecting to: ");
    Serial.println(g_wifi_ssid.c_str());
    Serial.println("Restarting...");
    #endif
    delay(1000);
    ESP.restart();
  }
}

void configRestServerRouting()
{
  g_webServer.on("/", HTTP_GET, []() {
    g_webServer.send(200, "text/html",
                          "Hello from Alarm controller REST Web Server");
  });

  // g_webServer.on("/trigger_alarm", HTTP_PUT, onPutTriggerAlarm);
  // g_webServer.on("/register_device", HTTP_PUT, onRegisterDevice);
  g_webServer.on("/esp_restart", HTTP_GET, onEspRestart);
}

void webServerStartup() {
  configRestServerRouting();

  g_webServer.begin();
  #ifdef _DEBUG
  Serial.println("HTTP REST Server Started");
  #endif
}

bool readDeviceId()
{
  uint8_t device_id = EEPROM.readUInt(c_device_id_addr);
  Serial.print("Device Id from eeprom: ");
  Serial.println(device_id);
  if (device_id > 0 && device_id < SIZE_MAX)
  {
    g_device_id = device_id;
    return true;
  }
  return false;
}

bool writeDeviceId()
{
  if(g_device_id > 0)
  {
    EEPROM.writeUInt(c_device_id_addr, g_device_id);
    EEPROM.commit();
    return true;
  }
  return false;
}

bool readAlarmId()
{
  uint8_t alarm_id = EEPROM.readUInt(c_alarm_id_addr);
  Serial.print("Alarm Id from eeprom: ");
  Serial.println(alarm_id);
  if (alarm_id > 0 && alarm_id < SIZE_MAX)
  {
    g_alarm_id = alarm_id;
    return true;
  }
  return false;
}

bool writeAlarmId()
{
  if(g_alarm_id > 0)
  {
    EEPROM.writeUInt(c_alarm_id_addr, g_alarm_id);
    EEPROM.commit();
    return true;
  }
  return false;
}

bool readWifiSsid()
{
  std::string wifi_ssid = EEPROM.readString(c_wifi_ssid_addr).c_str();
  Serial.print("Wifi SSID from eeprom: ");
  Serial.println(wifi_ssid.c_str());
  if (!wifi_ssid.empty())
  {
    g_wifi_ssid = wifi_ssid;
    return true;
  }
  return false;
}

bool writeWifiSsid()
{
  if(!g_wifi_ssid.empty())
  {
    EEPROM.writeString(c_wifi_ssid_addr, String(g_wifi_ssid.c_str()));
    EEPROM.commit();
    return true;
  }
  return false;
}

bool readWifiPassword()
{
  std::string wifi_password = EEPROM.readString(c_wifi_password_addr).c_str();
  Serial.print("Wifi password from eeprom: ");
  Serial.println(wifi_password.c_str());
  if (!wifi_password.empty())
  {
    g_wifi_password = wifi_password;
    return true;
  }
  return false;
}

bool writeWifiPassord()
{
  if(!g_wifi_password.empty())
  {
    EEPROM.writeString(c_wifi_password_addr, String(g_wifi_password.c_str()));
    EEPROM.commit();
    return true;
  }
  return false;
}

class BTServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
      Serial.println("Device connected to BLEServer!");
      g_bt_state = bt_state_::STATE_BT_CONNECTED;
    };

    void onDisconnect(BLEServer* pServer) {
      Serial.println("Device disconnected from BLEServer!");
      g_bt_state = bt_state_::STATE_BT_DISCONNECTED;
    }
};

class BTCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *pCharacteristic) {
      std::string value = pCharacteristic->getValue();

      Serial.print("VALUE: ");
      Serial.println(value.c_str());
      if (value.size() > 0) {

        if(g_bt_state != bt_state_::STATE_BT_CONNECTED) {
          g_bt_state = bt_state_::STATE_BT_CONNECTED;
        }

        Serial.print("New value: ");
        for (int i = 0; i < value.length(); i++)
          Serial.print(value[i]);

        Serial.println();
        if(pCharacteristic->getUUID().toString() == c_wifi_ssid_characteristic_uuid) {
          g_wifi_ssid = value;
          Serial.print("Setting WIFI SSID: ");
          Serial.println(g_wifi_ssid.c_str());
          if(!writeWifiSsid()) {
            Serial.println("Could not store the Wifi SSID to EEPROM!!!");
          }
          // pCharacteristic->setValue((uint8_t*)&value, 1);
        } else if(pCharacteristic->getUUID().toString() == c_wifi_password_characteristic_uuid) {
          g_wifi_password = value;
          Serial.print("Setting WIFI Password: ");
          Serial.println(g_wifi_password.c_str());
          if(!writeWifiPassord()) {
            Serial.println("Could not store the Wifi password to EEPROM!!!");
          }
          // pCharacteristic->setValue((uint8_t*)&value, 1);
        } else if(pCharacteristic->getUUID().toString() == c_alarm_characteristic_uuid) {
          Serial.print("Setting alarm_id value: ");
          Serial.println(value.c_str());
          g_alarm_id = atoi(value.c_str());
          Serial.print("Setting alarm_id: ");
          Serial.println(g_alarm_id);
          if(!writeAlarmId()) {
            Serial.println("Could not store the AlarmId to EEPROM!!!");
          }
          // pCharacteristic->setValue((uint8_t*)&value, 1);
        } else if(pCharacteristic->getUUID().toString() == c_device_characteristic_uuid) {
          Serial.print("Setting device_id value: ");
          Serial.println(value.c_str());
          g_device_id = atoi(value.c_str());
          Serial.print("Setting device_id: ");
          Serial.println(g_device_id);
          if(!writeDeviceId()){
            Serial.println("Could not store the DeviceId to EEPROM!!!");
          }
          // pCharacteristic->setValue((uint8_t*)&value, 1);
        }
      }
    }
};

BLECharacteristic *getAlarmCharacteristic(BLEService *pService) {
  return pService->
    createCharacteristic(
                          c_alarm_characteristic_uuid,
                          BLECharacteristic::PROPERTY_READ |
                          BLECharacteristic::PROPERTY_WRITE
                        );
}

BLECharacteristic *getDeviceCharacteristic(BLEService *pService) {
  return pService->
    createCharacteristic(
                          c_device_characteristic_uuid,
                          BLECharacteristic::PROPERTY_READ |
                          BLECharacteristic::PROPERTY_WRITE
                        );
}

BLECharacteristic *getWifiSsidCharacteristic(BLEService *pService) {
  return pService->
    createCharacteristic(
                          c_wifi_ssid_characteristic_uuid,
                          BLECharacteristic::PROPERTY_READ |
                          BLECharacteristic::PROPERTY_WRITE
                        );
}

BLECharacteristic *getWifiPasswordCharacteristic(BLEService *pService) {
  return pService->
    createCharacteristic(
                          c_wifi_password_characteristic_uuid,
                          BLECharacteristic::PROPERTY_READ |
                          BLECharacteristic::PROPERTY_WRITE
                        );
}

void PostSensorMeasurements() {
  unsigned long current_millis = millis();
  if (current_millis < g_lastPostMillis + c_period && !g_firstRun)
    return;

  g_firstRun = false;
  float t = 0.0, h = 0.0, p = 0.0, a = 0.0;
  int g = 0;
  int n_measurements = 10;
  for (int i = 0; i < n_measurements; ++i)
  {
    t += bme.readTemperature();
    h += bme.readHumidity();
    p += bme.readPressure() / 100.0F;
    a += bme.readAltitude(SEALEVELPRESSURE_HPA);
    g += analogRead(c_gasPin);
    delay(250);
  }

  float temperature = t / n_measurements;
  float humidity = h / n_measurements;
  float pressure = p / n_measurements;
  float altitude = a / n_measurements;
  int gas = g / n_measurements;

  if (gas >= 600)
    g_securityAlarmOn = 1;
  else
    g_securityAlarmOn = 0;

  g_lastPostMillis = millis();
  // if ((g_lastTemperatureVal == temperature && g_lastHumidityVal == humidity && g_lastGasVal == gas)
  //     || temperature == 0 || temperature == NULL
  //     || humidity == 0 || humidity == NULL
  //     || gas == 0 || gas == NULL)
  //   return;

  g_lastTemperatureVal = temperature;
  g_lastHumidityVal = humidity;
  g_lastPressureVal = pressure;
  g_lastAltitudeVal = altitude;
  g_lastGasVal = gas;

#ifdef _DEBUG
  Serial.print("Temperature = ");
  Serial.print(temperature);
  Serial.println(" degree celsius");
  Serial.print("Humidity = ");
  Serial.print(humidity);
  Serial.println(" %");
  Serial.print("Pressure = ");
  Serial.print(pressure);
  Serial.println(" hPa");
  Serial.print("Altitude = ");
  Serial.print(altitude);
  Serial.println(" m");
  Serial.print("gas: ");
  Serial.println(gas);
#endif
  StaticJsonDocument<200> doc;
  doc["temperature"] = temperature;
  doc["humidity"] = humidity;
  doc["pressure"] = pressure;
  doc["altitude"] = altitude;
  doc["gas_value"] = gas;
  String body;
  serializeJson(doc, body);
#ifdef _DEBUG
  Serial.print("Environment body: ");
  Serial.println(body);
#endif
  String resource = c_resSensorValues + "/" + String(g_device_id) + "?alarmId=" + String(g_alarm_id);
  Serial.print("Device Id: ");
  Serial.println(g_device_id);
  Serial.print("resource: ");
  Serial.println(resource);
  String payload;
  int statusCode = httpPost(HOME_SERVER_ADDRESS, HOME_SERVER_REST_PORT, resource.c_str(), body.c_str(), payload);
#ifdef _DEBUG
  Serial.print("Response status from home server: ");
  Serial.println(statusCode);
#endif
}

void setup() {
  Serial.begin(115200);
  EEPROM.begin(80);
  Serial.println();
  g_bt_state = bt_state_::STATE_BT_INITIALIZED;
  g_wifi_state = wifi_state_::STATE_WIFI_INITIALIZED;

  for(int i=0;i<80;i++) {
    EEPROM.write(i, 0);
    EEPROM.commit();
  }

  if(readWifiSsid() && readWifiPassword()
     && readDeviceId() && readAlarmId()) {
    wifiStartup();
    webServerStartup();
  } else {
    BLEDevice::init("SmartHome Environment");
    BLEServer *pServer = BLEDevice::createServer();

    BLEService *pService = pServer->createService(c_service_uuid);

    BLECharacteristic *alarm_characteristic = getAlarmCharacteristic(pService);
    alarm_characteristic->setCallbacks(new BTCallbacks());

    BLECharacteristic *device_characteristic = getDeviceCharacteristic(pService);
    device_characteristic->setCallbacks(new BTCallbacks());

    BLECharacteristic *wifi_ssid_characteristic = getWifiSsidCharacteristic(pService);
    wifi_ssid_characteristic->setCallbacks(new BTCallbacks());

    BLECharacteristic *wifi_password_characteristic = getWifiPasswordCharacteristic(pService);
    wifi_password_characteristic->setCallbacks(new BTCallbacks());

    pService->start();
    BLEAdvertising *pAdvertising = pServer->getAdvertising();
    pAdvertising->start();
  }

  pinMode(c_securityAlarmPin, OUTPUT);
  digitalWrite(c_securityAlarmPin, LOW);
  pinMode(c_gasFireAlarmPin, OUTPUT);
  // noTone(c_gasFireAlarmPin);
  pinMode(c_gasPin, INPUT);

  bme.begin(0x76);

  g_lastTemperatureVal = 0;
  g_lastHumidityVal = 0;
  g_lastPressureVal = 0;
  g_lastAltitudeVal = 0;
  g_lastGasVal = 0;
}

void loop() {
  uint16_t delay_ms = 2000;
  Serial.print("g_bt_state: ");
  Serial.println(g_bt_state);
  Serial.print("g_wifi_state: ");
  Serial.println(g_wifi_state);
  switch (g_wifi_state) {
  case wifi_state_::STATE_WIFI_INITIALIZED:
  case wifi_state_::STATE_WIFI_DISCONNECTED:
    if(!g_wifi_ssid.empty() && !g_wifi_password.empty()) {
      wifiStartup();
      webServerStartup();
    }
    break;
  case wifi_state_::STATE_WIFI_CONNECTED:
    delay_ms = 100;
    g_webServer.handleClient();
    // triggerGasFireAlarm();
    // triggerAlarm();
    PostSensorMeasurements();
    break;
  default:
    break;
  }
  delay(delay_ms);
}
