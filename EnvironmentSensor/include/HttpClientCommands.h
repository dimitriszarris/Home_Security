// HttpClientCommands.h

#ifndef _HTTPCLIENTCOMMANDS_h
#define _HTTPCLIENTCOMMANDS_h

#if defined(ARDUINO) && ARDUINO >= 100
#include "Arduino.h"
#else
#include "WProgram.h"
#endif

#include <HTTPClient.h>

/*
typedef struct header_{
  String key_;
  String value_;
}Header;

typedef struct headers_{
  Header* headers;
  headers_(){headers = NULL; size_ = 0;}
  headers_(Header* headers) {
    if(headers)
      size_ = sizeof(headers) / sizeof(Header);
    else
      size_ = 0;
  }
  int push_back(Header h) {
    headers = new Header(h);
    size_++;
  }
  int size() { return size_; }
  int size_;
}Headers;
*/
int httpGet(const char* host, int port, const char* resource, String &payload)
{
  Serial.print("In httpGet 0 ");
  HTTPClient http;

  Serial.print("In httpGet 1 ");
  delay(1000);
  http.begin(String("http://") + String(host) + ":" + String(port) + String(resource));
  //Send the request
  int httpCode = http.GET();
  Serial.print("In httpGet 2 ");
  delay(1000);
  payload = "";
  if (httpCode > 0)
  { //Check the returning code
    payload = http.getString(); //Get the request response payload
    //Serial.println(payload);
  }

  http.end(); //Close connection
  return httpCode;
}

int httpPost(const char* host, int port, const char *resource, const char* body, String& payload)
{
  HTTPClient http;    //Declare object of class HTTPClient

  http.begin(String("http://") + host + ":" + String(port) + String(resource));
  http.addHeader("Content-Type", "application/json");    //Specify content-type header

  int httpCode = http.POST(body);   //Send the request
  payload = http.getString();    //Get the response payload

  http.end();  //Close connection
  return httpCode;
}


#endif
