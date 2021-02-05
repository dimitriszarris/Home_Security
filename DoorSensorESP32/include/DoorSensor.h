// DoorSensor.h

#ifndef _DOORSENSOR_h
#define _DOORSENSOR_h

#include "Arduino.h"

class DoorSensor
{
protected:
	int reed_pin;
public:
	DoorSensor(int pin) { reed_pin = pin; }
	~DoorSensor() {}

	void init() { pinMode(reed_pin, INPUT_PULLUP);	}
	bool is_door_closed()
	{
		uint16_t readval = digitalRead(reed_pin); // Check the pin
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
};

#endif
