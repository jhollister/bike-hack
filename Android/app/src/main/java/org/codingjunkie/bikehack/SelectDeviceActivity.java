package org.codingjunkie.bikehack;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.ArrayList;
import java.util.Set;

public class SelectDeviceActivity extends AppCompatActivity {
    private final String TAG = SelectDeviceActivity.class.getSimpleName();

    // ListView stuff
    ArrayList<BluetoothDevice> listOfBlueDevices = null;
    BlueDeviceAdapter blueDeviceAdapter = null;
    ListView blueListView = null;
    View blueCurrentSelected = null;
    int blueCurrentSelectedIndex = -1;
    ColorStateList blueDefaultColors = null;

    // Bluetooth stuff
    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);

        FloatingActionButton refreshBtn = (FloatingActionButton) findViewById(
                R.id.blueListViewBtnRefresh);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoSelectRow();
                populate();
            }
        });

        FloatingActionButton goBtn = (FloatingActionButton) findViewById(
                R.id.blueListViewBtnGo);
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectDeviceActivity.this, MainActivity.class);
                if (blueCurrentSelectedIndex < 0) {
                    Toast.makeText(SelectDeviceActivity.this, "Device selection required!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("blueName", listOfBlueDevices.get(
                        blueCurrentSelectedIndex).getName());
                intent.putExtra("blueAddress", listOfBlueDevices.get(
                        blueCurrentSelectedIndex).getAddress());
                startActivity(intent);
            }
        });
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

        // Initialize activity
        init();

        // Fill up listview with data
        if (!populate()) {
            Toast.makeText(this, "Failed to populate list with bonded devices.", Toast.LENGTH_LONG)
            .show();
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
        // ListView stuff
        if (listOfBlueDevices == null) {
            listOfBlueDevices = new ArrayList<>();
        }

        if (blueDeviceAdapter == null) {
            blueDeviceAdapter = new BlueDeviceAdapter(this, listOfBlueDevices);
        }

        if (blueListView == null) {
            blueListView = (ListView) findViewById(R.id.blueListView);
        }

        blueListView.setAdapter(blueDeviceAdapter);

        blueListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (blueCurrentSelected != view) {
                    undoSelectRow();
                }

                blueCurrentSelected = view;
                blueCurrentSelectedIndex = position;
                doSelectRow();
            }
        });

        // Bluetooth stuff
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.d(TAG, "Bluetooth is not supported!");
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private boolean populate() {
        if (bluetoothAdapter == null) {
            return false;
        }

        listOfBlueDevices.clear();
        listOfBlueDevices.addAll(bluetoothAdapter.getBondedDevices());
        blueDeviceAdapter.notifyDataSetChanged();
        Toast.makeText(SelectDeviceActivity.this, "Devices loaded.", Toast.LENGTH_SHORT).show();

        return true;
    }

    private void doSelectRow() {
        if (blueCurrentSelected == null) {
            return;
        }

        // Store default state colors
        if (blueDefaultColors == null) {
            blueDefaultColors = ((TextView) blueCurrentSelected.findViewById(R.id.device_name))
                    .getTextColors();
        }

        blueCurrentSelected.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        TextView blueName = (TextView) blueCurrentSelected.findViewById(R.id.device_name);
        blueName.setTextColor(getResources().getColor(android.R.color.white));
        TextView blueAddress = (TextView) blueCurrentSelected.findViewById(R.id.device_address);
        blueAddress.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void undoSelectRow() {
        if (blueCurrentSelected == null) {
            return;
        }

        blueCurrentSelected.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        TextView blueName = (TextView) blueCurrentSelected.findViewById(R.id.device_name);
        blueName.setTextColor(blueDefaultColors);
        TextView blueAddress = (TextView) blueCurrentSelected.findViewById(R.id.device_address);
        blueAddress.setTextColor(blueDefaultColors);

        blueCurrentSelected = null;
        blueCurrentSelectedIndex = -1;
    }
}
