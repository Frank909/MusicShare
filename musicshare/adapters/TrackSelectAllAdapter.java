package com.sms.musicshare.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sms.musicshare.R;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.ItemTouchHelperAdapter;
import com.sms.musicshare.helper.ItemTouchHelperViewHolder;
import com.sms.musicshare.helper.OnCheckBoxClickListener;
import com.sms.musicshare.helper.OnStartDragListener;
import com.sms.musicshare.helper.Prova;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackSelectAllAdapter extends RecyclerView.Adapter<TrackSelectAllAdapter.TrackSelectAllView>{

    private Context mContext;
    private List<InfoTrack> mTrackList;
    private static List<Boolean> mItemChecked;
    private TrackSelectAllView viewHolder;
    private DBHelper mDBHelper;
    private int idPlaylist;
    private final OnCheckBoxClickListener mCheckBoxClickListener;

    // Pass in the contact array into the constructor
    public TrackSelectAllAdapter(Context context, List<InfoTrack> tracks, List<Boolean> bool, int id_playlist, OnCheckBoxClickListener listener) {
        mTrackList = tracks;
        mContext = context;
        mItemChecked = bool;
        idPlaylist = id_playlist;
        mDBHelper = new DBHelper(context);
        mCheckBoxClickListener = listener;
    }

    public TrackSelectAllAdapter(Context context, List<InfoTrack> tracks, List<Boolean> bool, OnCheckBoxClickListener listener) {
        mTrackList = tracks;
        mContext = context;
        mItemChecked = bool;
        mDBHelper = new DBHelper(context);
        mCheckBoxClickListener = listener;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public TrackSelectAllView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View trackView = inflater.inflate(R.layout.track_selectall_row, parent, false);
        // Return a new holder instance
        viewHolder = new TrackSelectAllView(trackView, mCheckBoxClickListener);
        return viewHolder;
    }

    public void initCheckboxes(){
        for(int i = 1; i<= mTrackList.size(); i++)
            mItemChecked.add(false);
    }

    @Override
    public void onBindViewHolder(final TrackSelectAllView holder, int position) {
        // Get the data model based on position
        InfoTrack infoTrack = mTrackList.get(position);

        // Set item views based on your views and data model
        holder.mTrack.setText(infoTrack.getTitle());
        holder.mArtist.setText(infoTrack.getArtist().getName());

        holder.mCheckbox.setChecked(mItemChecked.get(position));
    }

    public void setChecked() {
        for(int i=0; i<mItemChecked.size(); i++)
            mItemChecked.set(i, true);
        bindViewHolder(viewHolder, 0);
    }

    public void setUnchecked() {
        for(int i=0; i<mItemChecked.size(); i++)
            mItemChecked.set(i, false);
        bindViewHolder(viewHolder, 0);
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    public void updateData(ArrayList<Prova> viewModels) {
        viewModels.clear();
        viewModels.addAll(viewModels);
        notifyDataSetChanged();
    }

    public void setFilter(List<InfoTrack> param) {
        mTrackList = new ArrayList<>();
        mTrackList.addAll(param);
        notifyDataSetChanged();
    }

    public List<Boolean> getListCheckedItems(){
        return mItemChecked;
    }

    public List<InfoTrack> getListTracks(){
        return mTrackList;
    }

    public TrackSelectAllView getHolder() {return viewHolder;}

    public static class TrackSelectAllView extends RecyclerView.ViewHolder implements CheckBox.OnClickListener{

        private TextView mTrack, mArtist;
        private CheckBox mCheckbox;
        private OnCheckBoxClickListener onCheckBoxClick;

        TrackSelectAllView(View itemView, OnCheckBoxClickListener listener) {
            super(itemView);

            mTrack = (TextView) itemView.findViewById(R.id.id_track_text_into_track_selectall_row);
            mArtist = (TextView) itemView.findViewById(R.id.id_artist_text_into_track_selectall_row);
            mCheckbox = (CheckBox) itemView.findViewById(R.id.id_checkBox_into_track_selectall_row);
            mCheckbox.setOnClickListener(this);

            onCheckBoxClick = listener;
        }

        @Override
        public void onClick(View view) {

                if(mCheckbox.isChecked()){
                    mItemChecked.set(getAdapterPosition(), true);
                    if(onCheckBoxClick != null)
                        onCheckBoxClick.OnCheckedCheckbox(view);
                }
                else{
                    mItemChecked.set(getAdapterPosition(), false);
                    if(onCheckBoxClick != null)
                        onCheckBoxClick.OnUncheckedCheckbox(view);
                }
        }
    }
}
