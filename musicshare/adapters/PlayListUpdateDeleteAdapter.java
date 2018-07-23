package com.sms.musicshare.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.PlaylistActivity;
import com.sms.musicshare.R;
import com.sms.musicshare.TrackPlaylistSelectionActivity;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.playlist.PlayList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayListUpdateDeleteAdapter extends RecyclerView.Adapter<PlayListUpdateDeleteAdapter.PlayListUpdateDeleteView>  {

    private Context mContext;
    private List<PlayList> mPlayList;
    private DBHelper mDBHelper;
    private String no_share_songs;
    private TextView mEmptyTextView;

    // Pass in the contact array into the constructor
    public PlayListUpdateDeleteAdapter(Context context, List<PlayList>playlist) {
        mPlayList = playlist;
        mContext = context;
        mDBHelper = new DBHelper(context);
        no_share_songs = context.getResources().getString(R.string.empty_list_playlist);
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public PlayListUpdateDeleteAdapter.PlayListUpdateDeleteView onCreateViewHolder(ViewGroup parent, int viewType) {
        mEmptyTextView = (TextView) parent.getRootView().findViewById(R.id.playlist_empty_textview);
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View playlistUpdateDeleteView = inflater.inflate(R.layout.playlist_update_delete_row, parent, false);

        // Return a new holder instance
        PlayListUpdateDeleteAdapter.PlayListUpdateDeleteView viewHolder = new PlayListUpdateDeleteAdapter.PlayListUpdateDeleteView(playlistUpdateDeleteView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlayListUpdateDeleteAdapter.PlayListUpdateDeleteView holder, int position) {
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

    class PlayListUpdateDeleteView  extends RecyclerView.ViewHolder{

        TextView mN_Tracks;
        TextView mPlaylist;
        ImageView mAlbumImage;
        ImageView mUpdateNamePlaylist;
        ImageView mDeletePlaylist;
        EditText mPlaylistTextName;
        AlertDialog mAlertDialog;

        PlayListUpdateDeleteView(View itemView) {
            super(itemView);

            mN_Tracks = (TextView) itemView.findViewById(R.id.id_NTracks_text_into_playlist_row);
            mPlaylist = (TextView) itemView.findViewById(R.id.id_playlist_text_into_playlist_row);
            mAlbumImage = (ImageView) itemView.findViewById(R.id.id_image_playlist_row);
            mUpdateNamePlaylist = (ImageView) itemView.findViewById(R.id.id_image_update_into_playlist_update_delete_row);
            mDeletePlaylist = (ImageView) itemView.findViewById(R.id.id_image_delete_into_playlist_update_delete_row);

            mDeletePlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.message_dialog_title)
                            .setMessage(R.string.delete_tracks_message)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mDBHelper.removePlaylist(mPlayList.get(getAdapterPosition()).getPlayListId());
                                    mPlayList.remove(getAdapterPosition());
                                    notifyDataSetChanged();
                                    if(mPlayList.size() == 0)
                                        mEmptyTextView.setText(no_share_songs);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });

            mUpdateNamePlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPlaylistTextName = new EditText(getContext());
                    mPlaylistTextName.setText(mPlayList.get(getAdapterPosition()).getPlayListName());
                    AlertDialog.Builder mCreatePlaylistAlertDialog = new AlertDialog.Builder(getContext());

                    mCreatePlaylistAlertDialog.setTitle(R.string.update_playlist_dialog_title);
                    mCreatePlaylistAlertDialog.setView(mPlaylistTextName);
                    mCreatePlaylistAlertDialog.setPositiveButton(R.string.playlist_update_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String txt = mPlaylistTextName.getText().toString();
                            if(txt.isEmpty())
                                Toast.makeText(getContext(), R.string.playlist_error_empty_name, Toast.LENGTH_SHORT).show();
                            else{
                                mDBHelper.updatePlaylistName(mPlayList.get(getAdapterPosition()).getPlayListId(), txt);
                                Toast.makeText(getContext(), R.string.playlist_modify_message, Toast.LENGTH_SHORT).show();
                                mPlayList.get(getAdapterPosition()).setPlaylist_name(txt);
                                notifyDataSetChanged();
                            }
                        }
                    });

                    mCreatePlaylistAlertDialog.setNegativeButton(android.R.string.no, null).show();

                    mAlertDialog = mCreatePlaylistAlertDialog.create();

                    mPlaylistTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                mAlertDialog.getWindow().setSoftInputMode(
                                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                    });
                }
            });
        }

    }
}
