#include <Adafruit_NeoPixel.h>
#include "pixel.h"
#include <SoftwareSerial.h>

PIXEL hack;

void setup(){
  hack.strip.begin();
  hack.strip.show();
  Serial.begin(9600);
  Serial.setTimeout(300);
  Serial.println("Starting up...");
}

void loop(){
    hack.update();
    hack.display();
}
