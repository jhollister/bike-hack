# BrightWheels (bike-hack)

BrightWheels is a good way to bring fun to safety. The purpose was to make bicycle lighting easy to implement 
with simple hardaware. 

## How to use

After installation and device has been powered on, open the BrightWheels phone app and do the following:
* Choose a `Pattern` (currently 4).
* Choose LED colors by selecting the LED then choosing the required `Color`(Default = Red).
* Press `Save` to transmit data.
* Sucess!

If a Pebble smartwatch is available, download the BrightWheels app for the Pebble. Once synched to the phone, patterns and off can be changed by flicking the wrist.

## Installation
Needed software or libraries:  
* `Adafruit NeoPixel Library` [Github Link](https://github.com/adafruit/Adafruit_NeoPixel "Adafruit_NeoPixel") 
* `Arduino IDE` [Arduino Software](https://www.arduino.cc/en/Main/Software)
* `Android Studio` [Android SDK](https://developer.android.com/sdk/index.html)
  
Compile and flash the `bike-hack/Ardiuno/Arduino.ino` using the Arduino IDE.




## Wiring
**WS2812 LEDs to Arduino Uno**  
* Wire the data in of the LEDs to PIN 6 of the Arduino. Can be changed in code.

WS2812 need Voltage of 5 Volts and a max of 60 mA per LED. A string of 30 will require 1.8 Volts at max brightness. For further details see the  [Adafruit Guide to NeoPixels(WS2812)](https://learn.adafruit.com/adafruit-neopixel-uberguide/overview "Adafruit neopixel uberguide").   

**HC-06 to Arduino Uno**  
  
* Connect the Tx pin of the Bluetooth module to the Rx pin of the arduino and Rx pin of the Bluetooth module to the Tx pin of the Arduino.  
  
See power specs on the specific bluetooth module for power needs.


## Required Hardware
* Arduino - Uno
* 30 Strand RGB LED - WS2812
* Bluetooth Module - HC-06

## Optional Hardware
Pebble Classic - smartwatch

## Upcoming
 Synchronization with fellow bicylists  
 Pattern Builder

## First appearance

University of California, Riverside
Citrus Hack Fall 2015
