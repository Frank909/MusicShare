package com.sms.musicshare.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sms.musicshare.R;
import com.sms.musicshare.helper.Info;

import java.util.ArrayList;

public class InfoAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Info> data;
    private static LayoutInflater inflater = null;

    public InfoAdapter(Context context, ArrayList<Info> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView title;
        TextView subTitle;
        ImageView image;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView =  inflater.inflate(R.layout.info_row, parent, false);
        title = (TextView) convertView.findViewById(R.id.title_view_in_info_row);
        subTitle = (TextView) convertView.findViewById(R.id.subtitle_view_in_info_row);
        image = (ImageView) convertView.findViewById(R.id.image_view_in_info_row);

        Info info = data.get(position);

        title.setText(info.getTitle());
        subTitle.setText(info.getSubTitle());
        image.setImageDrawable(info.getImage());
        image.setBackgroundColor(Color.TRANSPARENT);

        switch (info.getID()){
            case Info.ID_REVIEW:
                image.setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
                break;
            case Info.ID_FEEDBACK:
                image.setColorFilter(Color.RED);
                break;
            case Info.ID_DEVELOPERS:
                image.setColorFilter(Color.BLUE);
                break;
        }

        return convertView;
    }
}