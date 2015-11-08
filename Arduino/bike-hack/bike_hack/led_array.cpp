#include "led_array.h"

LED_Array::LED_Array(int size) {
    array_size = size;
    led_array = new LED[array_size];
    clear();
}

LED_Array::~LED_Array(void) {
    delete[] led_array;
}

boolean LED_Array::available(void) {
    return bluetooth.available();
}

int LED_Array::length(void) {
    return array_size;
}

void LED_Array::fetch_leds(void) {
    String msg;
    msg = bluetooth.read_string();
    bluetooth.write_string(msg);
    int num_leds = msg.length() / LED_LENGTH;
    int msg_index = 0;
    for (int i = 0; i < array_size; i++) {
        if (i < num_leds) {
			bluetooth.write_string(String(msg[msg_index]));
			bluetooth.write_string(String(msg[msg_index+1]));
			bluetooth.write_string(String(msg[msg_index+2]));
            led_array[i].red = msg[msg_index] == '1' ? 255 : 0;
            led_array[i].green = msg[msg_index + 1] == '1' ? 255 : 0;
            led_array[i].blue = msg[msg_index + 2] == '1' ? 255 : 0;
            msg_index += 3;
        }
        else {
            led_array[i].red = 0;
            led_array[i].green = 0;
            led_array[i].blue = 0;
        }
    }
}

void LED_Array::clear(void){
	for(int i(0) ; i < array_size ; i++) {
		led_array[i].red = 0;
		led_array[i].green = 0;
		led_array[i].blue = 0;
	}
}
