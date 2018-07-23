package com.sms.musicshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.adapters.PlayListAdapter;
import com.sms.musicshare.adapters.PlayListUpdateDeleteAdapter;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.listeners.RecyclerItemClickListener;
import com.sms.musicshare.playlist.PlayList;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContextualPlaylistActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.context_playlist_empty_textview)TextView mEmptyListTextView;
    @BindView(R.id.context_playlist_fab) FloatingActionButton fab;
    @BindView(R.id.id_context_playlist_list) RecyclerView rvPlaylists;

    @BindString(R.string.empty_list_playlist) String empty_list_playlist;
    @BindString(R.string.track_added_to_playlist) String track_added_to_playlist;
    @BindString(R.string.track_added_error) String track_added_error;
    @BindString(R.string.create_playlist_dialog_title) String playlist_dialog_title;
    @BindString(R.string.playlist_error_empty_name) String playlist_empty_error;
    @BindString(R.string.playlist_error_message) String playlist_error_message;
    @BindString(R.string.playlist_success_message) String playlist_success_message;
    @BindString(R.string.playlist_button) String playlist_button;
    @BindString(R.string.playlist_button_cancel) String playlist_button_cancel;

    private AlertDialog.Builder mCreatePlaylistAlertDialog;
    private AlertDialog mAlertDialog;
    private DBHelper mDBHelper;
    private ArrayList<PlayList> mPlayList;
    private InfoTrack mTrack;
    private PlayListAdapter adapter;
    private RecyclerItemClickListener adapterListener;
    private EditText mPlaylistTextName;

    @OnClick(R.id.context_playlist_fab) void FabOnClick(){mAlertDialog.show();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contextual_playlist);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Playlist");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTrack = getIntent().getParcelableExtra("TrackForPlaylist");
        mDBHelper = new DBHelper(this);
        mPlayList = mDBHelper.getAllPlayLists();

        if(mPlayList.isEmpty()){
            mEmptyListTextView.setText(empty_list_playlist);
        }else {
            adapter = new PlayListAdapter(this, mPlayList);
            rvPlaylists.setAdapter(adapter);
            rvPlaylists.setLayoutManager(new LinearLayoutManager(this));

            adapterListener = new RecyclerItemClickListener(this, rvPlaylists, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    int track_id = mDBHelper.getTrackID(mTrack.getPath());
                    if(track_id == -1) {
                        int playlist_id = mDBHelper.getPlaylistID(mPlayList.get(position).getPlayListName());
                        mDBHelper.insertOneTrackInPlayList(mTrack, playlist_id);
                        Toast.makeText(ContextualPlaylistActivity.this, track_added_to_playlist, Toast.LENGTH_SHORT).show();
                        finish();
                    }else
                        Toast.makeText(ContextualPlaylistActivity.this, track_added_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            rvPlaylists.addOnItemTouchListener(adapterListener);
        }

        /****************************************************************************************/
        /*
            Dialog per il Floating Action Button per la creazione di una nuova playlist
         */

        mPlaylistTextName = new EditText(this);
        mCreatePlaylistAlertDialog = new AlertDialog.Builder(this);

        mCreatePlaylistAlertDialog.setTitle(playlist_dialog_title);
        mCreatePlaylistAlertDialog.setView(mPlaylistTextName);
        mCreatePlaylistAlertDialog.setPositiveButton(playlist_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String txt = mPlaylistTextName.getText().toString();
                if(txt.isEmpty())
                    Toast.makeText(ContextualPlaylistActivity.this, playlist_empty_error, Toast.LENGTH_SHORT).show();
                else{
                    if(!mDBHelper.insertPlaylist(txt)){
                        Toast.makeText(ContextualPlaylistActivity.this, playlist_error_message, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ContextualPlaylistActivity.this, playlist_success_message, Toast.LENGTH_SHORT).show();
                        int playlist_id = mDBHelper.getPlaylistID(txt);
                        mDBHelper.insertOneTrackInPlayList(mTrack, playlist_id);
                        Toast.makeText(ContextualPlaylistActivity.this, track_added_to_playlist, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

        mCreatePlaylistAlertDialog.setNegativeButton(playlist_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mAlertDialog = mCreatePlaylistAlertDialog.create();
        float dpi = this.getResources().getDisplayMetrics().density;
        mAlertDialog.setView(mPlaylistTextName, (int)(19*dpi), (int)(5*dpi), (int)(14*dpi), (int)(5*dpi) );
        mPlaylistTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAlertDialog.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        /****************************************************************************************/
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
