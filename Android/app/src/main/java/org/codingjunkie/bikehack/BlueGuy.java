package org.codingjunkie.bikehack;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by torcherist on 11/7/15.
 */
public class BlueGuy {
    private final String TAG = BlueGuy.class.getSimpleName();
    private final String macAddress = "20:C9:D0:BA:44:2C";
    // Bluetooth Dongle HC-06 MAC Address
    // 98:D3:31:40:0B:7E
    private static final int REQUEST_ENABLE_BT = 1;
    private Context context;
    private BluetoothAdapter myAdapter;
    private BluetoothDevice myDevice;
    private BluetoothSocket mySocket;
    private UUID myUUID;
    private InputStream sin;
    private OutputStream sout;

    public BlueGuy(Context context) {
        assert context == null;

        this.context = context;
        myAdapter = BluetoothAdapter.getDefaultAdapter();

        if(init()) {
            if(connect(macAddress)) {
                Log.d(TAG, "Succeeded to connect!");
                setReadWritable();
                if(write("Hello from " + android.os.Build.MANUFACTURER + android.os.Build.PRODUCT +
                "!\n")) {
                    Log.d(TAG, "Write successful");
                } else {
                    Log.d(TAG, "Write failed");
                }
            }
        } else {
            Log.d(TAG, "Failed to init!");
        }
    }

    public boolean init() {
        if (!isGood()) {
            return false;
        }

        // Enable Bluetooth
        if (!myAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        return true;
    }

    public boolean connect(String address) {
        BluetoothSocket tmpSocket = null;
        myDevice = myAdapter.getRemoteDevice(address);
        myUUID = myDevice.getUuids()[0].getUuid();

        try {
            Method meth = myDevice.getClass().getMethod("createInsecureRfcommSocketToServiceRecord",
                    new Class[] {UUID.class});
            tmpSocket = (BluetoothSocket) meth.invoke(myDevice, myUUID);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        mySocket = tmpSocket;
        myAdapter.cancelDiscovery();
        if (mySocket != null) {
            try {
                Log.d(TAG, "MyUUID: " + myUUID.toString());
                mySocket.connect();
                Log.d(TAG, "Now Connected!");
            } catch (IOException e1) {
                e1.printStackTrace();
                try {
                    mySocket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }

        return mySocket != null;
    }

    public boolean setReadWritable() {
        try {
            sin = mySocket.getInputStream();
            sout = mySocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean write(String msg) {
        byte[] msgBuffer = msg.getBytes();
        try {
            sout.write(msgBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean isGood() {
        if (myAdapter == null) {
            return false;
        }
        return true;
    }

    public void close() {
        try {
            if (sin != null) {
                sin.close();
                sin = null;
            }

            if (sout != null) {
                sout.close();
                sout = null;
            }

            if (mySocket != null) {
                mySocket.close();
                mySocket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.d(TAG, "BT closed!");
        }
    }
}
