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
* `Pebble SDK` [Pebble SDK](https://developer.getpebble.com/sdk/)
  
*Arduino*: Compile and flash the `bike-hack/Ardiuno/Arduino.ino` using the Arduino IDE.

*Android*: We must launch Android Studio to import our Gradle Project. After launch we choose to **Import project (Eclipse ADT, Gradle, etc)**. You will be met with a directory chooser. Navigate to and choose the `bike-hack/Android` directory of this repository. Android Studio will create the necessary project files for you. Follow the Android instructions on how to [prepare](http://developer.android.com/tools/device.html) your device for [running/debugging](http://developer.android.com/tools/building/building-studio.html) the app onto your phone.

*Pebble*: Make sure to install the required Pebble developer software by visiting the [Pebble Developer site](https://developer.getpebble.com/) (Pebble SDK 3.6.2). Our Pebble app is written in C. Copy or have your Pebble project's working directory be our `bike-hack/Pebble` directory. Follow the instructions on the Pebble Developer site on how to upload apps to the smartwatch.

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

## First Appearance

University of California, Riverside
Citrus Hack Fall 2015
