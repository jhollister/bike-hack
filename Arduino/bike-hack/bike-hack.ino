#include <Arduino.h>
#include "bluetooth.h"
#include "led_array.h"
#include <SoftwareSerial.h>

Bluetooth bluetooth;
String msg;
LED_Array led_array;

void setup() {
    // put your setup code here, to run once:
}

void loop() {
    if (led_array.available()) {
        led_array.fetch_leds();
        for (int i = 0; i < led_array.length(); i++) {
            if (led_array[i].red != 0) {
                bluetooth.write_string("LED" + String(i) + ": RED ON");
            }
            else {
                bluetooth.write_string("LED" + String(i) + ": RED OFF");
            }
            if (led_array[i].green != 0) {
                bluetooth.write_string("LED" + String(i) + ": GREEN ON");
            }
            else {
                bluetooth.write_string("LED" + String(i) + ": GREEN OFF");
            }
            if (led_array[i].blue != 0) {
                bluetooth.write_string("LED" + String(i) + ": BLUE ON");
            }
            else {
                bluetooth.write_string("LED" + String(i) + ": BLUE OFF");
            }
        }
    }
    delay(100);// prepare for next data ...
}


