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
    public:
        LED_Array(int size);
        ~LED_Array(void);
        boolean available(void);
        LED* fetch_array(void);
        int get_length(void);
        LED get_led(int index);
    private:
        LED* led_array;
        Bluetooth bluetooth;
        int array_size;
};

#endif
