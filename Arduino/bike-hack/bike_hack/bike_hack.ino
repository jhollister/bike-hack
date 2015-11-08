#include "pixel.h"

void setup(){
  strip.begin();
  strip.show();
  pixel hack;
}

void loop(){
    hack.update();
    hack.display();
}
