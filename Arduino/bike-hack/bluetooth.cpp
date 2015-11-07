#include "bluetooth.h"

Bluetooth::Bluetooth(void) : BluetoothSerial(BT_RX, BT_TX) {
    BluetoothSerial.begin(9600);
    BluetoothSerial.println("Bluetooth on...");
}

boolean Bluetooth::available(void) {
    return BluetoothSerial.available();
}

void Bluetooth::write_string(String snd_msg) {
    BluetoothSerial.println(snd_msg);
}

void Bluetooth::write(char data) {
    BluetoothSerial.write(data);
}

String Bluetooth::read_string(void) {
    String msg = "";
    char c = ' ';
    while (c != '\n' && c != 0) {
        if (BluetoothSerial.available()) {
            c = char(BluetoothSerial.read());
            if (c != '\n')
                msg += c;
        }
    }
    return msg;
}

