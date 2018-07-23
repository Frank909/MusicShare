package com.sms.musicshare.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sms.musicshare.AlbumActivity;
import com.sms.musicshare.FavoritesActivity;
import com.sms.musicshare.InsidePlaylistActivity;
import com.sms.musicshare.PlaylistActivity;
import com.sms.musicshare.R;
import com.sms.musicshare.SettingsActivity;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.helper.ContextualMenu;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.MusicBackGroundService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;

public class MyMusicSharedAdapter extends RecyclerView.Adapter<MyMusicSharedAdapter.MyMusicSharedView>{

    private Context mContext;
    private ArrayList<InfoTrack> mTrackList;
    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private String dialog_title, delete_tracks_message, no_share_songs;
    private DBHelper dbHelper;
    private TextView mEmptyTextView;

    // Pass in the contact array into the constructor
    public MyMusicSharedAdapter(Context context, ArrayList<InfoTrack> tracks) {
        mTrackList = tracks;
        mContext = context;
        dialog_title = context.getResources().getString(R.string.message_dialog_title);
        delete_tracks_message = context.getResources().getString(R.string.delete_tracks_message);
        no_share_songs = context.getResources().getString(R.string.no_shared_songs);
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public MyMusicSharedView onCreateViewHolder(ViewGroup parent, int viewType) {
        mEmptyTextView = (TextView) parent.getRootView().findViewById(R.id.id_my_content_music_shared_empty_textview);
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View trackView = inflater.inflate(R.layout.my_music_shared_row, parent, false);

        // Return a new holder instance
        MyMusicSharedView viewHolder = new MyMusicSharedView(trackView, getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyMusicSharedView holder, int position) {
        // Get the data model based on position
        InfoTrack track = mTrackList.get(position);

        // Set item views based on your views and data model
        holder.mTrack.setText(track.getTitle());
        holder.mArtist.setText(track.getArtist().getName());
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    private void updateData(List<InfoTrack> viewModels) {
        mTrackList.clear();
        mTrackList.addAll(viewModels);
        notifyDataSetChanged();
    }

    public void setFilter(List<InfoTrack> param) {
        //mTrackList.clear();
        mTrackList = new ArrayList<>();
        mTrackList.addAll(param);
        notifyDataSetChanged();
    }

    public List<InfoTrack> getListTracks(){
        return mTrackList;
    }

    class MyMusicSharedView extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTrack, mArtist;
        private ImageView mDelete;
        private Context context;

        MyMusicSharedView(View itemView, Context context) {
            super(itemView);
            this.context = context;

            mTrack = (TextView) itemView.findViewById(R.id.id_track_text_into_my_music_shared_row);
            mArtist = (TextView) itemView.findViewById(R.id.id_artist_text_into_my_music_shared_row);
            mDelete = (ImageView) itemView.findViewById(R.id.id_image_delete_into_my_music_shared_row);

            mDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(context)
                    .setTitle(dialog_title)
                    .setMessage(delete_tracks_message)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            InfoTrack track = mTrackList.get(getAdapterPosition());
                            dbHelper = new DBHelper(context);
                            dbHelper.removeTrackFromSharedMusic(track.getPath());
                            mTrackList.clear();
                            mTrackList.addAll(dbHelper.getAllSharedTracks());
                            notifyDataSetChanged();

                            if(mTrackList.size() == 0)
                                mEmptyTextView.setText(no_share_songs);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }
}
