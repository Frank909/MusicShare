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

public class TrackDragSelectAllAdapter extends RecyclerView.Adapter<TrackDragSelectAllAdapter.TrackDragSelectAllView> implements ItemTouchHelperAdapter{

    private Context mContext;
    private List<InfoTrack> mTrackList;
    private static List<Boolean> mItemChecked;
    private TrackDragSelectAllView viewHolder;
    private DBHelper mDBHelper;
    private int idPlaylist;
    private final OnStartDragListener mDragStartListener;
    private final OnCheckBoxClickListener mCheckBoxClickListener;

    // Pass in the contact array into the constructor
    public TrackDragSelectAllAdapter(Context context, List<InfoTrack> tracks, List<Boolean> bool, int id_playlist, OnStartDragListener dragStartListener, OnCheckBoxClickListener listener) {
        mTrackList = tracks;
        mContext = context;
        mItemChecked = bool;
        idPlaylist = id_playlist;
        mDragStartListener = dragStartListener;
        mDBHelper = new DBHelper(context);
        mCheckBoxClickListener = listener;
    }

    public TrackDragSelectAllAdapter(Context context, List<InfoTrack> tracks, List<Boolean> bool, OnStartDragListener dragStartListener, OnCheckBoxClickListener listener) {
        mTrackList = tracks;
        mContext = context;
        mItemChecked = bool;
        mDragStartListener = dragStartListener;
        mDBHelper = new DBHelper(context);
        mCheckBoxClickListener = listener;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public TrackDragSelectAllView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View trackView = inflater.inflate(R.layout.track_drag_selectall_row, parent, false);
        // Return a new holder instance
        viewHolder = new TrackDragSelectAllView(trackView, mCheckBoxClickListener);
        return viewHolder;
    }

    public void initCheckboxes(){
        for(int i = 1; i<= mTrackList.size(); i++)
            mItemChecked.add(false);
    }

    @Override
    public void onBindViewHolder(final TrackDragSelectAllView holder, int position) {
        // Get the data model based on position
        InfoTrack infoTrack = mTrackList.get(position);

        // Set item views based on your views and data model
        holder.mTrack.setText(infoTrack.getTitle());
        holder.mArtist.setText(infoTrack.getArtist().getName());

        holder.mCheckbox.setChecked(mItemChecked.get(position));

        // Start a drag whenever the handle view it touched
        holder.mMoveItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
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

    public TrackDragSelectAllView getHolder() {return viewHolder;}

    @Override
    public void onItemDismiss(int position) {
        if(idPlaylist != -1)
            mDBHelper.removeTrackFromPlaylist(idPlaylist, mDBHelper.getTrackID(mTrackList.get(position).getPath()));
        mTrackList.remove(position);
        mItemChecked.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mTrackList, fromPosition, toPosition);
        Collections.swap(mItemChecked, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public static class TrackDragSelectAllView extends RecyclerView.ViewHolder implements CheckBox.OnClickListener, ItemTouchHelperViewHolder{

        private TextView mTrack, mArtist;
        private ImageView mMoveItem;
        private CheckBox mCheckbox;
        private OnCheckBoxClickListener onCheckBoxClick;
        private Drawable default_drawable;

        TrackDragSelectAllView(View itemView, OnCheckBoxClickListener listener) {
            super(itemView);

            mTrack = (TextView) itemView.findViewById(R.id.id_track_text_into_track_drag_selectall_row);
            mArtist = (TextView) itemView.findViewById(R.id.id_artist_text_into_track_drag_selectall_row);
            mCheckbox = (CheckBox) itemView.findViewById(R.id.id_checkBox_into_track_drag_selectall_row);
            mMoveItem = (ImageView) itemView.findViewById(R.id.id_image_move_up_down_into_track_drag_selectall_row);
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
        @Override
        public void onItemSelected() {
            this.default_drawable = itemView.getBackground();
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackground(this.default_drawable);
        }
    }
}
