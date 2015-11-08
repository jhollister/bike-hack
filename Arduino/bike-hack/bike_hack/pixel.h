#ifndef PIXEL_H
#define PIXEL_H

#include <Adafruit_NeoPixel.h>
#include "led_array.h"

#define PIN 6

class PIXEL {

	private:
		void blink(void);	//pattern 1
		void follow(void); 	//pattern 2
		void squeeze(void); //patter 3
		void shift(void);	//pattern 4
		void colorWipe(void);	//set pixel - 1 at a time 
		void colorWipeAll(void);//set pixel - all at once
		LED_Array Neo;
		int size = Neo.length();
		Adafruit_NeoPixel strip = Adafruit_NeoPixel(size,NEO_RGB + NEO_KHZ800);
	public:
		void update(void);
		void display(unsigned int pattern);
};

#endif

//Adafruit_NeoPixel strip = Adafruit_NeoPixel(30,NEO_RGB + NEO_KHZ800);
//LED_Array Neo;

//void setup(){
//  strip.begin();
//  strip.show();
 // Neo.clear();
//}

//void loop(){
   /*colorWipe(strip.Color(255,0,0),50);*/
//   displayArray();
//   delay(500);
 //  colorWipe2(strip.Color(0,0,0),500);
//}

