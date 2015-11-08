#include <Adafruit_NeoPixel.h>
#include "led_array.h"

#define PIN 6

Adafruit_NeoPixel strip = Adafruit_NeoPixel(30,NEO_RGB + NEO_KHZ800);

void setup(){
  strip.begin();
  strip.show();
  Led_Array Neo;
}

void loop(){
   //colorWipe(strip.Color(255,0,0),50);
   displayArray();
   delay(500);
   colorWipe2(strip.Color(0,0,0),500);
}
void displayArray(){
  if(Neo.available()){
    Neo.fetch_leds();
  }
    
    for(uint16_t i = 0; i < Neo.length(); i++){
      strip.setPixelColor(i,strip.Color(Neo[i].red,Neo[i].green,Neo[i].blue));
      strip.show();
      delay(50);
    }
}

void colorWipe(uint32_t c, uint8_t wait){
   for(uint16_t i = 0; i <strip.numPixels();i++) {
    strip.setPixelColor(i, c);
    strip.show();
    delay(wait);
   } 
}

void colorWipe2(uint32_t c, uint8_t wait){
   for(uint16_t i = 0; i <strip.numPixels();i++) {
    strip.setPixelColor(i, c);
   } 
   strip.show();
   delay(2*wait);
}
