package org.codingjunkie.bikehack;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private String wheelAddress = "";
    private String wheelName = "";
    private final int numLEDs = 30;
    private Integer ledIndex = 0;
    private BlueGuy wheel = null;
    private ConcurrentHashMap<String, Integer> data = new ConcurrentHashMap<String, Integer>();
    private List ledColors;
    private String pattern;
    private String color;
    private Thread bThread = null;
    private final Object bThreadLock = new Object();
    private boolean bThreadPaused = false;
    private boolean runBlueThread = true;


    public static Handler uiHandler = null;
    private BroadcastReceiver blueReceiver = null;
    private IntentFilter blueIntentFilter = null;

    //Pebble
    private PebbleKit.PebbleDataReceiver mReceiver;
    private final static UUID PEBBLE_APP_UUID = UUID.fromString(
            "6c7c9dac-6100-4790-8e3e-a0938ac89545");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate!");

        init();

        setupInputActions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
//            startActivity(settingsIntent);
//            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart!");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume!");

        setTextViewValue(R.id.TextViewPatternsValue, pattern);
        setTextViewValue(R.id.TextViewColorsValue, color);
        setTextViewValue(R.id.TextViewColors, "RGB LED" + ledIndex.toString() + ":");

        if (bThread != null) {
            if (!bThread.isAlive()) {
                bThread.start();
            }
        }

        unpauseBlueThread();

        // Get information back from the watch app
        if(mReceiver == null) {
            mReceiver = new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {

                @Override
                public void receiveData(Context context, int id, PebbleDictionary d) {
                    // Always ACKnowledge the last message to prevent timeouts
                    PebbleKit.sendAckToPebble(getBaseContext(), id);

                    // Get action and display
                    int state = d.getInteger(5).intValue();
                    switch (state) {
                        case 0:
                            ((Switch) findViewById(R.id.main_switch2)).setChecked(false);
                            break;
                        default:
                            ((Switch) findViewById(R.id.main_switch2)).setChecked(true);
                            pattern = Integer.toString(state);
                    }
                    setTextViewValue(R.id.TextViewPatternsValue, pattern);
                    send();
                    Toast.makeText(MainActivity.this, "Pattern from Pebble: " +
                                    Integer.toString(state), Toast.LENGTH_SHORT).show();
                }
            };
        }

        // Register the receiver to get data
        PebbleKit.registerReceivedDataHandler(MainActivity.this, mReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause!");

        pauseBlueThread();

        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy!");

        data.clear();
        runBlueThread = false;
        bThread = null;
        wheel.close();

        // Clean up bluetoothdevice broadcast receiver
        unregisterReceiver(blueReceiver);
        finish();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed!");
        finish();
    }

    private void init() {
        pattern = "1";
        color = "0100";
        data.put("bt", 1);
        data.put("on", 1);
        ledColors = new ArrayList();
        for (int i = 0; i < numLEDs; i++) {
            ledColors.add("0100");
        }

        // Setup bluetoothdevice receiver
        if (blueReceiver == null) {
            blueReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    switch (action) {
                        case BluetoothDevice.ACTION_ACL_CONNECTED:
                            Log.d(TAG, "BTA CONNECTED");
                            pauseBlueThread();
                            break;
                        case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                            Log.d(TAG, "BTA DISCONNECTED");
                            uiHandler.sendEmptyMessage(1);
                            wheel.close();
                            if (data.get("bt") == 1) {
                                unpauseBlueThread();
                            }
                            break;
                    }
                }
            };
        }

        // setup bindings for bluetoothdevice broadcast receiver
        if (blueIntentFilter == null) {
            blueIntentFilter = new IntentFilter();
            blueIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            blueIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            registerReceiver(blueReceiver, blueIntentFilter);
        }

        // Setup handler for UI events
        if (uiHandler == null) {
            uiHandler = new Handler() {
                public void handleMessage(Message msg) {
                    try {
                        switch (msg.what) {
                            case 0:
                                Toast.makeText(MainActivity.this, "BrightWheels is now connected.",
                                        Toast.LENGTH_SHORT).show();
                                pauseBlueThread();
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, "BrightWheels is disconnected.",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "CAUGHT EXCEPTION!");
                    }
                }
            };
        }

        if (bThread == null) {
            bThread = new Thread(new BlueConnectionThread());
        }

        Bundle blueValues = getIntent().getExtras();
        if (blueValues == null) {
            return;
        }

        wheelAddress = blueValues.getString("blueAddress");
        wheelName = blueValues.getString("blueName");
    }

    private void setTextViewValue(int id, String str) {
        TextView tmp = (TextView)findViewById(id);
        tmp.setText(str);
    }

    private void send() {
        String tmpString = "";
        String tmpColor = "0000";

        if (wheel == null || data.get("bt") != 1 || !wheel.isConnected()) {
            Toast.makeText(MainActivity.this, "Bluetooth not connected!",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "NO BT!");
            return;
        }

        if (data.get("on") == 0) {
            tmpColor = "";
        }

        for (int i = 0; i < numLEDs; i++) {
            tmpString += (tmpColor.equals("") ? "0000" : ledColors.get(i));
        }

        if(!wheel.write(pattern + binToHex(tmpString) + "\n")) {
            Log.d(TAG, "Failed to write to wheel! Make sure you are connected to remote device.");
        } else {
            Toast.makeText(MainActivity.this, "Message was sent to device.",
                    Toast.LENGTH_SHORT).show();
        }


        boolean isConnected = PebbleKit.isWatchConnected(this);
        if (isConnected) {
            String msg = "Pattern: " + pattern + "\n" + "Colors:\n" + colorsToString();
            if (data.get("on") == 0) {
                msg = "Off";
            }

            // Push a notification
            final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

            final Map notifData = new HashMap();
            notifData.put("title", "BikeHack");
            notifData.put("body", msg);
            final JSONObject jsonData = new JSONObject(notifData);
            final String notificationData = new JSONArray().put(jsonData).toString();

            i.putExtra("messageType", "PEBBLE_ALERT");
            i.putExtra("sender", "BikeHack");
            i.putExtra("notificationData", notificationData);
            sendBroadcast(i);
        }
    }


    public String binToHex(String bin) {
        String tmp = "";
        int len = bin.length();
        for (int i = 1; i < len + 1; i++) {
            if ((i > 0) && (i % 4 == 0)) {
                String tf = bin.substring(0, 4);
                bin = bin.substring(4);
                tmp += Integer.toString(Integer.parseInt(tf, 2), 16);
            }
        }
        return tmp;
    }

    public Integer incLedIndex() { return (ledIndex < numLEDs - 1) ? ledIndex++ : ledIndex; }

    public Integer decLedIndex() {
        return (ledIndex > 0) ? ledIndex-- : ledIndex;
    }

    public String colorsToString() {
        String tmp = "";
        for (int i = 0; i < numLEDs; i++) {
            tmp += "LED" + Integer.toString(i) + ": " + ledColors.get(i).toString() + "\n";
        }
        return tmp;
    }

    private void pauseBlueThread() {
        Log.d(TAG, "pausedBlueThread!");
        synchronized (bThreadLock) {
            bThreadPaused = true;
        }
    }

    private void unpauseBlueThread() {
        Log.d(TAG, "unpausedBlueThread!");
        synchronized (bThreadLock) {
            bThreadPaused = false;
            bThreadLock.notifyAll();
        }
    }

    private void setupInputActions() {
        Switch btSwitch = (Switch)  findViewById(R.id.main_switch1);
        Switch onOffSwitch = (Switch)  findViewById(R.id.main_switch2);

        btSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("btSwitch State=", "" + isChecked);
                data.put("bt", isChecked ? 1 : 0);
                if (data.get("bt") == 0) {
                    wheel.close();
                    pauseBlueThread();
                } else {
                    unpauseBlueThread();
                }
            }

        });

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("onOffSwitch State=", "" + isChecked);
                data.put("on", isChecked ? 1 : 0);
            }

        });

        final Button button1 = (Button) findViewById(R.id.btnPat1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pattern = "1";
                setTextViewValue(R.id.TextViewPatternsValue, pattern);
            }
        });

        final Button button2 = (Button) findViewById(R.id.btnPat2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pattern = "2";
                setTextViewValue(R.id.TextViewPatternsValue, pattern);
            }
        });

        final Button button3 = (Button) findViewById(R.id.btnPat3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pattern = "3";
                setTextViewValue(R.id.TextViewPatternsValue, pattern);
            }
        });

        final Button button4 = (Button) findViewById(R.id.btnPat4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pattern = "4";
                setTextViewValue(R.id.TextViewPatternsValue, pattern);
            }
        });

        final Button buttonSend = (Button) findViewById(R.id.btnSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                send();
            }
        });

        // Button Colors
        final Button buttonColor1 = (Button) findViewById(R.id.btnColr1);
        buttonColor1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ledColors.set(ledIndex, "0100");
                setTextViewValue(R.id.TextViewColorsValue, ledColors.get(ledIndex).toString());
            }
        });

        final Button buttonColor2 = (Button) findViewById(R.id.btnColr2);
        buttonColor2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ledColors.set(ledIndex, "0010");
                setTextViewValue(R.id.TextViewColorsValue, ledColors.get(ledIndex).toString());
            }
        });

        final Button buttonColor3 = (Button) findViewById(R.id.btnColr3);
        buttonColor3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ledColors.set(ledIndex, "0001");
                setTextViewValue(R.id.TextViewColorsValue, ledColors.get(ledIndex).toString());
            }
        });

        final Button buttonColor4 = (Button) findViewById(R.id.btnColr4);
        buttonColor4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ledColors.set(ledIndex, "0011");
                setTextViewValue(R.id.TextViewColorsValue, ledColors.get(ledIndex).toString());
            }
        });

        final Button buttonColor5 = (Button) findViewById(R.id.btnColr5);
        buttonColor5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ledColors.set(ledIndex, "0101");
                setTextViewValue(R.id.TextViewColorsValue, ledColors.get(ledIndex).toString());
            }
        });

        final Button buttonColor6 = (Button) findViewById(R.id.btnColr6);
        buttonColor6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ledColors.set(ledIndex, "0110");
                setTextViewValue(R.id.TextViewColorsValue, ledColors.get(ledIndex).toString());
            }
        });

        final Button buttonColor7 = (Button) findViewById(R.id.btnColr7);
        buttonColor7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ledColors.set(ledIndex, "0111");
                setTextViewValue(R.id.TextViewColorsValue, ledColors.get(ledIndex).toString());
            }
        });

        final Button buttonColor8 = (Button) findViewById(R.id.btnColr8);
        buttonColor8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ledColors.set(ledIndex, "0000");
                setTextViewValue(R.id.TextViewColorsValue, ledColors.get(ledIndex).toString());
            }
        });

        final Button buttonIncIndex = (Button) findViewById(R.id.btnLedUp);
        buttonIncIndex.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer tmp = incLedIndex();
                setTextViewValue(R.id.TextViewColors, "RGB LED" + ledIndex.toString() + ":");
                setTextViewValue(R.id.TextViewColorsValue, ledColors.get(ledIndex).toString());
            }
        });

        final Button buttonDecIndex = (Button) findViewById(R.id.btnLedDown);
        buttonDecIndex.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer tmp = decLedIndex();
                setTextViewValue(R.id.TextViewColors, "RGB LED" + ledIndex.toString() + ":");
                setTextViewValue(R.id.TextViewColorsValue, ledColors.get(ledIndex).toString());
            }
        });
    }

    public class BlueConnectionThread implements Runnable {
        private boolean init = true;
        public void run() {
            while(runBlueThread) {
                if (init || !wheel.isConnected()) {
                    if (data.get("bt") == 1) {
                        if (doConnect()) {
                            uiHandler.sendEmptyMessage(0);
                            Log.d(TAG, "Bluetooth now connected!");
                            pauseBlueThread();
                        } else {
                            Log.d(TAG, "Bluetooth failed to connect.");
                        }
                    }
                    init = false;
                }

                Log.d(TAG, "BlueConnectionThread cycle!");

                synchronized (bThreadLock) {
                    while (bThreadPaused) {
                        try {
                            bThreadLock.wait();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                SystemClock.sleep(1500);
            }
            Log.d(TAG, "BlueConnectionThread stopped");
        }

        private boolean doConnect() {
            if (wheel == null) {
                wheel = new BlueGuy(MainActivity.this);
                return wheel.connect(wheelAddress) && wheel.setReadWritable();
            } else {
                if (wheel.isGood()) {
                    wheel.close();
                    if (wheel.connect(wheelAddress)) {
                        wheel.setReadWritable();
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
