#include "bluetooth.h"

Bluetooth::Bluetooth(void) { // : BluetoothSerial(BT_RX, BT_TX) {
    Serial.begin(9600);
    Serial.setTimeout(300);
}

boolean Bluetooth::available(void) {
    return Serial.available();
}

void Bluetooth::write_string(String snd_msg) {
    Serial.println(snd_msg);
}

void Bluetooth::write(char data) {
    Serial.write(data);
}

String Bluetooth::read_string(void) {
    String msg = "";
    if (!Serial.available()) {
        return msg;
    }
    else {
        msg = Serial.readString();
    }
    return msg;
}

