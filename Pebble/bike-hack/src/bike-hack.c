/*
 * src/main.c
 * Creates a Window and two TextLayers to display instructions and feedback 
 * from the background worker. 
 */

#include <pebble.h>

#define WORKER_TICKS 0
#define KEY_DATA 5

static Window *s_main_window;
static TextLayer *s_output_layer, *s_ticks_layer, *s_message_layer;

static void send_int(int key, int value) {
    DictionaryIterator *iter;
    app_message_outbox_begin(&iter);
    dict_write_int(iter, key, &value, sizeof(int), true);
    app_message_outbox_send();
}

static void inbox_received_callback(DictionaryIterator *iterator, void *context) {
  // Get the first pair
  Tuple *data = dict_find(iterator, KEY_DATA);
  if (data) {
    static char s_buffer[32];
    snprintf(s_buffer, sizeof(s_buffer), "Received '%s'", data->value->cstring);
    text_layer_set_text(s_message_layer, s_buffer);
  }
}

static void inbox_dropped_callback(AppMessageResult reason, void *context) {
  APP_LOG(APP_LOG_LEVEL_ERROR, "Message dropped!");
}

static void outbox_failed_callback(DictionaryIterator *iterator, AppMessageResult reason, void *context) {
  APP_LOG(APP_LOG_LEVEL_ERROR, "Outbox send failed!");
}

static void outbox_sent_callback(DictionaryIterator *iterator, void *context) {
  APP_LOG(APP_LOG_LEVEL_INFO, "Outbox send success!");
}

static void worker_message_handler(uint16_t type, AppWorkerMessage *data) {
  if (type == WORKER_TICKS) { 
    // Read ticks from worker's packet
    int ticks = data->data0;

    // Show to user in TextLayer
    static char s_buffer[32];
    snprintf(s_buffer, sizeof(s_buffer), "pattern: %d", ticks);
    text_layer_set_text(s_ticks_layer, s_buffer);
    send_int(KEY_DATA, ticks);
  }
}

static void select_click_handler(ClickRecognizerRef recognizer, void *context) {
  // Check to see if the worker is currently active
  bool running = app_worker_is_running();
  if (!running) {
    text_layer_set_text(s_output_layer, "Shake to cycle patterns");
  }
  else {
    text_layer_set_text(s_output_layer, "Use SELECT to start/stop background process.");
  }



  // Toggle running state
  AppWorkerResult result;
  if (running) {
    result = app_worker_kill();

    if (result == APP_WORKER_RESULT_SUCCESS) {
      text_layer_set_text(s_ticks_layer, "Worker stopped!");
    } else {
      text_layer_set_text(s_ticks_layer, "Error killing worker!");
    }
  } else {
    result = app_worker_launch();

    if (result == APP_WORKER_RESULT_SUCCESS) {
      text_layer_set_text(s_ticks_layer, "Worker launched!");
    } else {
      text_layer_set_text(s_ticks_layer, "Error launching worker!");
    }
  }

  APP_LOG(APP_LOG_LEVEL_INFO, "Result: %d", result);
}

static void click_config_provider(void *context) {
  window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
}

static void main_window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect window_bounds = layer_get_bounds(window_layer);

  // Create output TextLayer
  s_message_layer = text_layer_create(GRect(0, 45, window_bounds.size.w - 5, window_bounds.size.h));
  text_layer_set_font(s_message_layer, fonts_get_system_font(FONT_KEY_GOTHIC_24));
  text_layer_set_text(s_message_layer, "Waiting...");
  text_layer_set_overflow_mode(s_message_layer, GTextOverflowModeWordWrap);
  layer_add_child(window_layer, text_layer_get_layer(s_message_layer));

  // Create UI
  s_output_layer = text_layer_create(GRect(0, 0, window_bounds.size.w, window_bounds.size.h));
  text_layer_set_text(s_output_layer, "Use SELECT to start/stop background process.");
  text_layer_set_text_alignment(s_output_layer, GTextAlignmentCenter);
  layer_add_child(window_layer, text_layer_get_layer(s_output_layer));

  s_ticks_layer = text_layer_create(GRect(5, 135, window_bounds.size.w, 30));
  text_layer_set_text(s_ticks_layer, "No data yet.");
  text_layer_set_text_alignment(s_ticks_layer, GTextAlignmentLeft);
  layer_add_child(window_layer, text_layer_get_layer(s_ticks_layer));
}

static void main_window_unload(Window *window) {
  // Destroy UI
  text_layer_destroy(s_output_layer);
  text_layer_destroy(s_ticks_layer);
}

static void init(void) {
    // Register callbacks
  app_message_register_inbox_received(inbox_received_callback);
  app_message_register_inbox_dropped(inbox_dropped_callback);
  app_message_register_outbox_failed(outbox_failed_callback);
  app_message_register_outbox_sent(outbox_sent_callback);

  // Open AppMessage with sensible buffer sizes
  app_message_open(64, 64);

  // Setup main Window
  s_main_window = window_create();
  window_set_click_config_provider(s_main_window, click_config_provider);
  window_set_window_handlers(s_main_window, (WindowHandlers) {
    .load = main_window_load,
    .unload = main_window_unload,
  });
  window_stack_push(s_main_window, true);

  // Subscribe to Worker messages
  app_worker_message_subscribe(worker_message_handler);
}

static void deinit(void) {
  // Destroy main Window
  window_destroy(s_main_window);

  // No more worker updates
  app_worker_message_unsubscribe();
}

int main(void) {
  init();
  app_event_loop();
  deinit();
}
