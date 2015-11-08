#include "led_array.h"

LED_Array::LED_Array(int size=DEFAULT_SIZE) {
    array_size = size;
    led_array = new LED[array_size];
}

LED_Array::~LED_Array(void) {
    delete[] led_array;
}

boolean LED_Array::available(void) {
    return bluetooth.available();
}

int LED_Array::get_length(void) {
    return array_size;
}

LED* LED_Array::fetch_array(void) {
    String msg;
    msg = bluetooth.read_string();
    int num_leds = msg.length() / LED_LENGTH;
    int msg_index = 0;
    for (int i = 0; i < array_size; i++) {
        if (i < num_leds) {
            led_array[i].state = true;
            led_array[i].red = msg[msg_index] == '1' ? 255 : 0;
            led_array[i].green = msg[msg_index + 1] == '1' ? 255 : 0;
            led_array[i].blue = msg[msg_index + 2] == '1' ? 255 : 0;
            msg_index += 3;
        }
        else {
            led_array[i].state = false;
            led_array[i].red = 0;
            led_array[i].green = 0;
            led_array[i].blue = 0;
        }
    }
    return led_array;
}
