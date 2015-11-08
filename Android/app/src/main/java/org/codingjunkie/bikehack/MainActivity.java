package org.codingjunkie.bikehack;

import android.app.Activity;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    private final String macAddress = "98:D3:31:40:0B:7E";
    private final int numLEDs = 30;
    private Integer ledIndex = 0;
    private BlueGuy bt = null;
    private HashMap<String, Integer> data = new HashMap<String, Integer>();
    private List ledColors;
    private String pattern;
    private String color;

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
                    bt.close();
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
            Toast.makeText(getApplicationContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "NO BT!");
            return;
        }

        if (data.get("on") == 0) {
            tmpColor = "";
        }

        for (int i = 0; i < numLEDs; i++) {
            tmpString += ((tmpColor == "") ? "0000" : ledColors.get(i));
        }

        bt.write(pattern + binToHex(tmpString) + "\n");
    }

    private boolean doConnect() {
        if (bt == null) {
            bt = new BlueGuy(getBaseContext());
        } else {
            if (bt.isGood()) {
                bt.close();
                if (bt.connect(macAddress)) {
                    bt.setReadWritable();
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
}
