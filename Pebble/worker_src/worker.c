/*
 * worker_src/worker.c
 * Launched and killed from the foreground app. Counts seconds passing 
 * and reports them using AppWorkerMessages.
 */

#include <pebble_worker.h>

#define WORKER_TICKS 0

static int32_t pattern = 1;

static void tap_handler(AccelAxisType axis, int32_t direction) {
  pattern = pattern < 4 ? pattern + 1 : 0;
  // Construct a data packet
  AppWorkerMessage msg_data = {
    .data0 = pattern
  };

  // Send the data to the foreground app
  app_worker_send_message(WORKER_TICKS, &msg_data);
}

static void worker_init() {
  // Use the TickTimer Service as a data source
  accel_tap_service_subscribe(tap_handler);

  // Choose update rate
  accel_service_set_sampling_rate(ACCEL_SAMPLING_10HZ);
}

static void worker_deinit() {
  accel_tap_service_unsubscribe();
}

int main(void) {
  worker_init();
  worker_event_loop();
  worker_deinit();
}

