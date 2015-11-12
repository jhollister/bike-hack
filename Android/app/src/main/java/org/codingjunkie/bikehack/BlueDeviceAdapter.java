package org.codingjunkie.bikehack;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by torcherist on 11/11/15.
 */
public class BlueDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
    Context context;

    public BlueDeviceAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        super(context, 0, devices);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        // retrieve item at position into temp variable
        BluetoothDevice blueTemp = getItem(position);

        // Try view or inflate it
        contentView = (contentView == null) ? LayoutInflater.from(this.context).inflate(
                R.layout.device_layout, parent, false) : contentView;

        // Get views from layout for data assignment
        TextView blueName = (TextView) contentView.findViewById(R.id.device_name);
        TextView blueAddress = (TextView) contentView.findViewById(R.id.device_address);

        // Do data assignment of BluetoothDevice values to TextView fields
        blueName.setText(blueTemp.getName());
        blueAddress.setText(blueTemp.getAddress());

        // Return the view
        return contentView;
    }

}
