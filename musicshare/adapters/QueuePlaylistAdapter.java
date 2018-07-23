package com.sms.musicshare.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

public class QueuePlaylistAdapter extends RecyclerView.Adapter<QueuePlaylistAdapter.QueuePlaylist> implements ItemTouchHelperAdapter{

    private Context mContext;
    private List<InfoTrack> mTrackList;
    private QueuePlaylist viewHolder;
    private DBHelper mDBHelper;
    private int idPlaylist;
    private final OnStartDragListener mDragStartListener;

    // Pass in the contact array into the constructor
    public QueuePlaylistAdapter(Context context, List<InfoTrack> tracks, int id_playlist, OnStartDragListener dragStartListener) {
        mTrackList = tracks;
        mContext = context;
        idPlaylist = id_playlist;
        mDragStartListener = dragStartListener;
        mDBHelper = new DBHelper(context);
    }

    public QueuePlaylistAdapter(Context context, List<InfoTrack> tracks, OnStartDragListener dragStartListener) {
        mTrackList = tracks;
        mContext = context;
        mDragStartListener = dragStartListener;
        mDBHelper = new DBHelper(context);
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public QueuePlaylist onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View trackView = inflater.inflate(R.layout.queue_playlist_row, parent, false);
        // Return a new holder instance
        viewHolder = new QueuePlaylist(trackView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final QueuePlaylist holder, int position) {
        // Get the data model based on position
        InfoTrack infoTrack = mTrackList.get(position);

        // Set item views based on your views and data model
        holder.mTrack.setText(infoTrack.getTitle());
        holder.mArtist.setText(infoTrack.getArtist().getName());
        String imageArtPath = infoTrack.getAlbum().getAlbumArt();
        if(!imageArtPath.equals("<unknown>"))
            holder.mAlbumArt.setImageDrawable(Drawable.createFromPath(imageArtPath));
        else
            holder.mAlbumArt.setImageResource(R.drawable.cd);

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

    public List<InfoTrack> getListTracks(){
        return mTrackList;
    }

    public QueuePlaylist getHolder() {return viewHolder;}

    @Override
    public void onItemDismiss(int position) {
        if(idPlaylist != -1)
            mDBHelper.removeTrackFromPlaylist(idPlaylist, mDBHelper.getTrackID(mTrackList.get(position).getPath()));
        mTrackList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mTrackList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public static class QueuePlaylist extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder{

        private TextView mTrack, mArtist;
        private ImageView mMoveItem;
        private ImageView mAlbumArt;
        private Drawable default_drawable;

        QueuePlaylist(View itemView) {
            super(itemView);

            mTrack = (TextView) itemView.findViewById(R.id.id_track_text_into_queue_playlist_row);
            mArtist = (TextView) itemView.findViewById(R.id.id_artist_text_into_queue_playlist_row);
            mAlbumArt = (ImageView) itemView.findViewById(R.id.id_queue_playlist_album_art_image);
            mMoveItem = (ImageView) itemView.findViewById(R.id.id_image_move_up_down_into_queue_playlist_row);
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
