package org.codingjunkie.bikehack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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


public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private final String macAddress = "98:D3:31:40:0B:7E";
    private final int numLEDs = 30;
    private Integer ledIndex = 0;
    private BlueGuy wheel = null;
    private HashMap<String, Integer> data = new HashMap<String, Integer>();
    private List ledColors;
    private String pattern;
    private String color;

    //Pebble
    private PebbleKit.PebbleDataReceiver mReceiver;
    private final static UUID PEBBLE_APP_UUID = UUID.fromString(
            "6c7c9dac-6100-4790-8e3e-a0938ac89545");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        init();

        Switch btSwitch = (Switch)  findViewById(R.id.switch1);
        Switch onOffSwitch = (Switch)  findViewById(R.id.switch2);

        btSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("btSwitch State=", "" + isChecked);
                data.put("bt", isChecked ? 1 : 0);

                if (data.get("bt") == 1) {
                    doConnect();
                } else {
                    wheel.close();
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

        if (data.get("bt") == 1) {
            doConnect();
        }

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
                            data.put("on", 0);
                            ((Switch) findViewById(R.id.switch2)).setChecked(false);
                            break;
                        default:
                            data.put("on", 1);
                            ((Switch) findViewById(R.id.switch2)).setChecked(true);
                            pattern = Integer.toString(state);
                    }
                    setTextViewValue(R.id.TextViewPatternsValue, pattern);
                    send();
                    Toast.makeText(getBaseContext(),
                            "Pattern from Pebble: " + Integer.toString(state),
                            Toast.LENGTH_SHORT).show();
                }
            };
        }

        // Register the receiver to get data
        PebbleKit.registerReceivedDataHandler(this, mReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause!");
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
    }

    private void setTextViewValue(int id, String str) {
        TextView tmp = (TextView)findViewById(id);
        tmp.setText(str);
    }

    private void send() {
        String tmpString = "";
        String tmpColor = "0000";

        if (data.get("bt") != 1) {
            Toast.makeText(getApplicationContext(), "Bluetooth not connected!",
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

        wheel.write(pattern + binToHex(tmpString) + "\n");


        boolean isConnected = PebbleKit.isWatchConnected(this);
        Toast.makeText(this, "Pebble " + (isConnected ? "is" : "is not") + " connected!",
                Toast.LENGTH_LONG).show();
        if (isConnected) {
            // Push a notification
            final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

            final Map data = new HashMap();
            data.put("title", "BikeHack");
            data.put("body", "Pattern: " + pattern + "\n" + "Colors:\n" + colorsToString());
            final JSONObject jsonData = new JSONObject(data);
            final String notificationData = new JSONArray().put(jsonData).toString();

            i.putExtra("messageType", "PEBBLE_ALERT");
            i.putExtra("sender", "BikeHack");
            i.putExtra("notificationData", notificationData);
            sendBroadcast(i);
        }
    }

    private boolean doConnect() {
        if (wheel == null) {
            wheel = new BlueGuy(getBaseContext());
            wheel.connect(macAddress);
            wheel.setReadWritable();
        } else {
            if (wheel.isGood()) {
                wheel.close();
                if (wheel.connect(macAddress)) {
                    wheel.setReadWritable();
                    Toast.makeText(getApplicationContext(), "Bluetooth connected.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth not connected!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
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

    public Integer incLedIndex() {
        return (ledIndex < numLEDs - 1) ? ledIndex++ : ledIndex;
    }

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
}
