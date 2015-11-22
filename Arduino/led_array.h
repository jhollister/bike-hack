#ifndef LED_ARRAY_H
#define LED_ARRAY_H

#include "bluetooth.h"

#define DEFAULT_SIZE 30
#define LED_LENGTH   3

struct {
    boolean state;
    unsigned char red;
    unsigned char green;
    unsigned char blue;
} typedef LED;

class LED_Array {
    private:
        LED* led_array;
        Bluetooth bluetooth;
        int array_size;
        int pattern;
        int hex_to_int(char);
    public:
        LED_Array(int size=DEFAULT_SIZE);
        ~LED_Array(void);
        boolean available(void);
        void fetch_leds(void);
		void clear(void);
        int length(void);
        int get_pattern(void);
        const LED& operator[](unsigned int i) const { return led_array[i]; }
};

#endif
