package com.sms.musicshare.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sms.musicshare.R;
import com.sms.musicshare.SharedMusicFromActivity;

import java.util.ArrayList;
import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceView>{

    private Context mContext;
    private ArrayList<String> mDeviceList;
    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    // Pass in the contact array into the constructor
    public DevicesAdapter(Context context, ArrayList<String> devices) {
        mDeviceList = devices;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public DeviceView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View deviceView = inflater.inflate(R.layout.devices_row, parent, false);

        // Return a new holder instance
        DeviceView viewHolder = new DeviceView(deviceView, getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final DeviceView holder, int position) {
        // Get the data model based on position
        String device = mDeviceList.get(position);

        // Set item views based on your views and data model
        holder.mDeviceName.setText(device);
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    private void updateData(List<String> viewModels) {
        mDeviceList.clear();
        mDeviceList.addAll(viewModels);
        notifyDataSetChanged();
    }

    public void setFilter(List<String> param) {
        //mTrackList.clear();
        mDeviceList = new ArrayList<>();
        mDeviceList.addAll(param);
        notifyDataSetChanged();
    }

    public List<String> getListTracks(){
        return mDeviceList;
    }

    class DeviceView extends RecyclerView.ViewHolder{

        private TextView mDeviceName;
        private ImageView mSmartphoneImage;
        private Context context;

        DeviceView(View itemView, Context context) {
            super(itemView);
            this.context = context;

            mDeviceName = (TextView) itemView.findViewById(R.id.id_device_name_into_devices_row);
            mSmartphoneImage = (ImageView) itemView.findViewById(R.id.id_image_devices_row);
        }
    }
}
