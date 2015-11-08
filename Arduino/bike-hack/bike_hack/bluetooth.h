#ifndef BLUETOOTH_H
#define BLUETOOTH_H

#include <Arduino.h>
#include <SoftwareSerial.h>

#define BT_TX 10
#define BT_RX 11

class Bluetooth {

    public:
        Bluetooth(void);
        boolean available(void);
        void write_string(String);
        void write(char data);
        String read_string(void);
    private:
        SoftwareSerial BluetoothSerial;
};

#endif
