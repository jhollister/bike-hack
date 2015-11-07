#include <Arduino.h>
#include "bluetooth.h"
#include <SoftwareSerial.h>

Bluetooth bluetooth;
String msg;

void setup() {
    // put your setup code here, to run once:
}

void loop() {
    if (bluetooth.available()) {
        msg = bluetooth.read_string();
        bluetooth.write_string(msg);
    }
    delay(100);// prepare for next data ...
}


