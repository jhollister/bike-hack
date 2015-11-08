#ifndef LED_ARRAY_H
#define LED_ARRAY_H

#include "bluetooth.h"

#define DEFAULT_SIZE 30
#define LED_LENGTH   3

struct {
    boolean state;
    char red;
    char green;
    char blue;
} typedef LED;

class LED_Array {
    private:
        LED* led_array;
        Bluetooth bluetooth;
        int array_size;
    public:
        LED_Array(int size=DEFAULT_SIZE);
        ~LED_Array(void);
        boolean available(void);
        void fetch_leds(void);
        int length(void);
        const LED& operator[](unsigned int i) const { return led_array[i]; }
};

#endif
