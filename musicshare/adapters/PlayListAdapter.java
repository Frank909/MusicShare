package com.sms.musicshare.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sms.musicshare.R;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.playlist.PlayList;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListView>  {

    private Context mContext;
    private DBHelper mDBHelper;
    private List<PlayList> mPlayList;

    // Pass in the contact array into the constructor
    public PlayListAdapter(Context context, List<PlayList>playlist) {
        mPlayList = playlist;
        mContext = context;
        mDBHelper = new DBHelper(mContext);
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public PlayListAdapter.PlayListView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View playlistView = inflater.inflate(R.layout.playlist_row, parent, false);

        // Return a new holder instance
        PlayListAdapter.PlayListView viewHolder = new PlayListAdapter.PlayListView(playlistView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlayListAdapter.PlayListView holder, int position) {
        // Get the data model based on position
        PlayList mPlayListName = mPlayList.get(position);

        // Set item views based on your views and data model
        holder.mPlaylist.setText(mPlayListName.getPlayListName());
        int counter = mDBHelper.countTracksInPlaylist(mPlayList.get(position).getPlayListId());
        holder.mN_Tracks.setText(counter + " " + getContext().getResources().getQuantityString(R.plurals.number_of_playlist_tracks, counter));
    }
    @Override
    public int getItemCount() {
        return mPlayList.size();
    }

    class PlayListView extends RecyclerView.ViewHolder{

        TextView mN_Tracks;
        TextView mPlaylist;
        ImageView mAlbumImage;

        PlayListView(View itemView) {
            super(itemView);

            mN_Tracks = (TextView) itemView.findViewById(R.id.id_NTracks_text_into_playlist_row);
            mPlaylist = (TextView) itemView.findViewById(R.id.id_playlist_text_into_playlist_row);
            mAlbumImage = (ImageView) itemView.findViewById(R.id.id_image_playlist_row);
        }
    }
}
