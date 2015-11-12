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

//public class UsersAdapter extends ArrayAdapter<User> {
//    public UsersAdapter(Context context, ArrayList<User> users) {
//        super(context, 0, users);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // Get the data item for this position
//        User user = getItem(position);
//        // Check if an existing view is being reused, otherwise inflate the view
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
//        }
//        // Lookup view for data population
//        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
//        TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
//        // Populate the data into the template view using the data object
//        tvName.setText(user.name);
//        tvHome.setText(user.hometown);
//        // Return the completed view to render on screen
//        return convertView;
//    }
//}
