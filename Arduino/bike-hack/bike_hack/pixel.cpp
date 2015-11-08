#include "pixel.h"

void PIXEL::blink(void){
	for(uint16_t i = 0; i < size; i++){
 		strip.setPixelColor(i,strip.Color(Neo[i].red,Neo[i].green,Neo[i].blue));
	}
	strip.show();
	delay(500);
}

void PIXEL::follow(void){
	for(uint16_t i = 0; i < size; i++){
		strip.setPixelColor(i,strip.Color(Neo[i].red,Neo[i].green,Neo[i].blue));
		strip.show();
		delay(50);
	}
}

void PIXEL::squeeze(void){
	uint16_t j = 0;
	for(uint16_t i = 0; i < (j = size-1-i); i++){
		strip.setPixelColor(i,strip.Color(Neo[i].red,Neo[i].green,Neo[i].blue));
		strip.setPixelColor(j,strip.Color(Neo[j].red,Neo[j].green,Neo[j].blue));
		strip.show();
		delay(50);
	}
}

void PIXEL::shift(void){
	for(uint16_t i = 0; i < size; i++){
		for(uint16_t j = size; j > 0 ; j--){
			strip.setPixelColor(j,strip.getPixelColor(j-1));
		}
		strip.setPixelColor(0,strip.Color(Neo[size-i-1].red,Neo[size-i-1].green,Neo[size-i-1].blue));
		strip.show();
		delay(50);
	}
}

void PIXEL::colorSet(uint32_t c, uint8_t wait){//set 1 color then show
	for(uint16_t i = 0; i <strip.numPixels();i++) {
		strip.setPixelColor(i, c);
		strip.show();
		delay(wait);
	} 
}

void PIXEL::colorSetAll(uint32_t c, uint8_t wait){//set all color then show

	for(uint16_t i = 0; i <strip.numPixels();i++) {
		strip.setPixelColor(i, c);
	} 
	strip.show();
}

void PIXEL::display(void){
    int pattern = Neo.get_pattern();
	switch(pattern){
		case 2:
			follow();
			delay(100);
			colorSetAll(strip.Color(0,0,0),250);
			break;
		case 3:
			squeeze();
			delay(100);
			colorSetAll(strip.Color(0,0,0),250);
			break;
		case 4:
			shift();
			delay(500);
			colorSetAll(strip.Color(0,0,0),250);
			break;
		case 1:
		default:
			blink();
			delay(500);
			colorSet(strip.Color(0,0,0),500);
	}
}

void PIXEL::update(void){
	if(Neo.available()) {
		Neo.fetch_leds();
    }
}
