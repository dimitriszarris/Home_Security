#include <ArduinoJson.h>
#include "WiFi.h"
#include "rom/rtc.h"
#include "driver/rtc_io.h"
#include "esp_sleep.h"
#include <esp_wifi.h>
#include <esp_bt.h>
#include "driver/adc.h"
// #include "D:\Development\PersonalProjects\Home_Security\Include\DoorSensor\DoorSensor.h"
#include "D:\Development\PersonalProjects\Home_Security\Include\BatteryVoltage.h"
#include "D:\Development\PersonalProjects\Home_Security\Include\ESP32\HttpClientCommands.h"
#include "D:\Development\PersonalProjects\Home_Security\Include\WifiConfiguration.h"
#include "D:\Development\PersonalProjects\Home_Security\Include\HomeServerSettings.h"
#include "D:\Development\PersonalProjects\Home_Security\Include\WifiCredentials.h"

const int buzzerPin = 5;
const int doorSensorPin = 2;
// DoorSensor doorSensor(doorSensorPin);

#define BUTTON_PIN_BITMASK 0x0004 // GPIO 2
#define _DEBUG
/*
struct {
  byte data[4];
}rtc_data;

void print_data()
{
  char buf[3];
  uint8_t *ptr = (uint8_t *)&rtc_data;
  for (size_t i = 0; i < sizeof(rtc_data); i++)
  {
    sprintf(buf, "%02X", ptr[i]);
    Serial.print(buf);
    if ((i + 1) % 32 == 0) {
      Serial.println();
    } else {
      Serial.print(" ");
    }
  }
  Serial.println();
}

bool isFirstRun()
{
  rtc_data.data[0] = 0x00;
  rtc_data.data[1] = 0x00;
  rtc_data.data[2] = 0x00;
  rtc_data.data[3] = 0x00;
  if(ESP.rtcUserMemoryRead(65, (uint32_t*) &rtc_data, sizeof(rtc_data)))
  {
    Serial.println("Data read!");
    print_data();
  }


  if(rtc_data.data[0] == 0x00)
  {
    Serial.println("First run, going to deep sleep immediatelly...");
    rtc_data.data[0] = 0x01;
    rtc_data.data[1] = 0x00;
    rtc_data.data[2] = 0x00;
    rtc_data.data[3] = 0x00;
    if(ESP.rtcUserMemoryWrite(65, (uint32_t*) &rtc_data, sizeof(rtc_data)))
    {
      Serial.println("Data written!");
      print_data();
    }
    espDeepSleep();
  }

}
*/

// Voltage measurement
// 29/04/2020 23:16   -   3.970V  -  3.0μA
void espDeepSleep()
{
  esp_sleep_enable_ext1_wakeup(BUTTON_PIN_BITMASK, ESP_EXT1_WAKEUP_ALL_LOW);

  btStop();
  adc_power_off();
  esp_bt_controller_disable();

  esp_deep_sleep_start();
}

void doorBuzzer(bool buzz)
{
  digitalWrite(buzzerPin, buzz ? HIGH : LOW);
}

int initWifi()
{
  int retries = 0;

  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWD);
  // check the status of WiFi connection to be WL_CONNECTED
  while ((WiFi.status() != WL_CONNECTED) && (retries < MAX_WIFI_INIT_RETRY))
  {
    retries++;
    delay(WIFI_RETRY_DELAY_MS);
  }

  return WiFi.status(); // return the WiFi connection status
}

int getAlarmState()
{
  int statusCode = 0, retries = 0;
  bool validResponse = false;
  int alarmState = -1;
  while ((statusCode != 200 || !validResponse) && retries < 3)
  {
    retries++;
    String response = "";
    statusCode = httpGet(HOME_SERVER_ADDRESS, HOME_SERVER_REST_PORT, "/alarm/1", response);
#ifdef _DEBUG
    Serial.print(String("Return code after calling: ") + String(HOME_SERVER_ADDRESS) + String(":") + String(HOME_SERVER_REST_PORT) + String("/alarm/1 statusCode: "));
    Serial.println(statusCode);
#endif
    if (statusCode != 200)
    {
      delay(100);
      continue;
    }
#ifdef _DEBUG
    Serial.print("Response body: ");
    Serial.println(response);
#endif
    StaticJsonDocument<200> doc;
    deserializeJson(doc, response);
    JsonObject obj = doc.as<JsonObject>();
    if (obj.isNull())
    {
      delay(100);
      continue;
    }
    validResponse = true;
    alarmState = obj["alarm_state"];
#ifdef _DEBUG
    Serial.print("Alarm state: ");
    Serial.println(alarmState);
#endif
    if (statusCode != 200 || !validResponse)
      delay(100);
  }

  return alarmState;
}

int postDoorState(bool doorState, String &payload)
{
  StaticJsonDocument<200> doc;
  doc["sensor_id"] = 1;
  doc["alarm_id"] = 0;
  doc["door_state"] = doorState;
  String body;
  serializeJson(doc, body);
#ifdef _DEBUG
  Serial.print("DoorState body: ");
  Serial.println(body);
#endif
  int statusCode = httpPost(HOME_SERVER_ADDRESS, HOME_SERVER_REST_PORT, "/door", body.c_str(), payload);
  return statusCode;
}

int postBatteryVoltage(String &payload)
{
  StaticJsonDocument<200> doc;
  float voltage = batteryVoltage(A6, 30000, 7500, 3.3, 4095.0, -.71); //batteryVoltage(A0, 20000, 1000, 3.3, 4095.0, .01);
  doc["sensor_id"] = 1;
  doc["alarm_id"] = 0;
  doc["battery_voltage"] = voltage;
  doc["battery_percentage"] = (voltage - 3.0) * 100.0 / (4.2 - 3.0);
  String body;
  serializeJson(doc, body);
#ifdef _DEBUG
  Serial.print("Battery body: ");
  Serial.println(body);
#endif
  int statusCode = httpPost(HOME_SERVER_ADDRESS, HOME_SERVER_REST_PORT, "/battery", body.c_str(), payload);
  return statusCode;
}

bool isDoorClosed()
{
  uint16_t readval = digitalRead(doorSensorPin); // Check the pin
  if (readval > 0)
  {
    //lcd.display_message("Door is opened!", 1);
#ifdef DEBUG
    Serial.print("Door Sensor read value: ");
    Serial.println(readval);
    Serial.println("Door open!");
#endif
    return false;
  }
  else
  {
    return true;
  }
}

void setup()
{
  Serial.begin(115200);

  setCpuFrequencyMhz(80);
  adc_power_on();

#ifdef _DEBUG_2
  Serial.println("CPU0 reset reason:");
  print_reset_reason(rtc_get_reset_reason(0));
  verbose_print_reset_reason(rtc_get_reset_reason(0));

  Serial.println("CPU1 reset reason:");
  print_reset_reason(rtc_get_reset_reason(1));
  verbose_print_reset_reason(rtc_get_reset_reason(1));
#endif

  if (initWifi() == WL_CONNECTED)
  {
#ifdef _DEBUG
    Serial.println();
    Serial.print("Connected to ");
    Serial.print(WIFI_SSID);
    Serial.println();
    Serial.print("IP: ");
    Serial.println(WiFi.localIP());
#endif
  }
  else
  {
#ifdef _DEBUG
    Serial.print("Error connecting to: ");
    Serial.println(WIFI_SSID);
    Serial.println("Restarting...");
#endif
    ESP.restart();
  }

  pinMode(buzzerPin, OUTPUT);

  if (getAlarmState() == 0 /* || isFirstRun()*/)
  {
    String payload;
    postBatteryVoltage(payload);
#ifdef _DEBUG
    Serial.println("Alarm is disarmed. Going to deep sleep immediatelly");
#endif
    espDeepSleep();
  }

  // Alarm is armed
  // doorSensor.init();
  pinMode(doorSensorPin, INPUT_PULLUP);
}

void loop()
{
  // Wait for door to close
  // bool doorState = doorSensor.is_door_closed();
  bool doorState = isDoorClosed();
#ifdef _DEBUG
  Serial.print("Door is: ");
  Serial.println(doorState ? "opened" : "closed");
#endif
  String payload;
  if (doorState > 0)
  {
    postDoorState(doorState, payload);
    postBatteryVoltage(payload);
  }
  long maxDoorOpenedPeriod = 60000; // The door can be open only for 1 min when the alarm is activated. Then a buzzer kicks off
  long doorOpenedMillis = millis();
  long currentMillis = millis();
  while (doorState > 0)
  {
    // doorState = doorSensor.is_door_closed();
    doorState = isDoorClosed();
#ifdef _DEBUG
    Serial.print("doorState: ");
    Serial.println(doorState);
#endif
    if (!doorState)
    {
      doorBuzzer(false);
#ifdef _DEBUG
      Serial.println("CLOSED DOOR");
#endif
      break;
    }
    long currentMillis = millis();
    if (currentMillis - doorOpenedMillis >= maxDoorOpenedPeriod)
    {
      doorBuzzer(true);
    }
    delay(1000);
  }
#ifdef _DEBUG
  Serial.print("doorState: ");
  Serial.println(doorState);
#endif
  if (!doorState)
  {
    int statusCode = postDoorState(doorState, payload);
#ifdef _DEBUG
    Serial.print("Post door_state response code: ");
    Serial.println(statusCode);
    Serial.print("Post door_state payload: ");
    Serial.println(payload);
#endif
    statusCode = postBatteryVoltage(payload);
#ifdef _DEBUG
    Serial.print("Post battery_voltage response code: ");
    Serial.println(statusCode);
    Serial.print("Post battery_voltage payload: ");
    Serial.println(payload);
    Serial.println("Door sensor is going back to deep sleep mode...");
#endif
    espDeepSleep();
  }
}
