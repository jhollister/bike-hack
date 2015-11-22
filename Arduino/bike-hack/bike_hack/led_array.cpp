#include "led_array.h"

LED_Array::LED_Array(int size) {
    array_size = size;
    pattern = 1;
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

int LED_Array::get_pattern(void) {
    return pattern;
}

int LED_Array::hex_to_int(char hex_char) {
    return (hex_char > '9')? (hex_char &~ 0x20) - 'A' + 10: (hex_char - '0');
}

void LED_Array::fetch_leds(void) {
    String msg = bluetooth.read_string();
    bluetooth.write_string(msg);
    msg.trim();
    int num_leds = (msg.length() - 1 > array_size) ? array_size: msg.length() - 1;
    bluetooth.write_string("Number of leds: " + String(num_leds));
    if (msg.length()) {
        pattern = hex_to_int(msg[0]);
        bluetooth.write_string("Pattern: " + String(pattern));
    }
    for (int i = 0; i < num_leds; i++) {
			int current_val = hex_to_int(msg[i+1]);
            led_array[i].red = current_val & 4 ? 255 : 0;
            led_array[i].green = current_val & 2 ? 255 : 0;
            led_array[i].blue = current_val & 1 ? 255 : 0;
            bluetooth.write_string("LED" + String(i)
                                    + " RED:" + String(led_array[i].red)
                                    + " GREEN:" + String(led_array[i].green)
                                    + " BLUE:" + String(led_array[i].blue));
    }
}

void LED_Array::clear(void){
	for(int i(0) ; i < array_size ; i++) {
		led_array[i].red = 0;
		led_array[i].green = 0;
		led_array[i].blue = 0;
	}
}
