package com.sms.musicshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.adapters.PlayListAdapter;
import com.sms.musicshare.adapters.PlayListUpdateDeleteAdapter;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.playlist.PlayList;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaylistActivity extends AppCompatActivity {

    @BindView(R.id.playlist_fragment_drag_player) FrameLayout mDragPlayerContainer;
    @BindView(R.id.id_playlist_list) RecyclerView rvPlaylists;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.playlist_fab) FloatingActionButton fab;
    @BindView(R.id.playlist_empty_textview) TextView mEmptyListTextView;

    @BindString(R.string.create_playlist_dialog_title) String playlist_dialog_title;
    @BindString(R.string.playlist_error_empty_name) String playlist_empty_error;
    @BindString(R.string.playlist_error_message) String playlist_error_message;
    @BindString(R.string.playlist_success_message) String playlist_success_message;
    @BindString(R.string.playlist_button) String playlist_button;
    @BindString(R.string.playlist_button_cancel) String playlist_button_cancel;
    @BindString(R.string.empty_list_playlist) String empty_list_playlist;

    private AlertDialog.Builder mCreatePlaylistAlertDialog;
    private AlertDialog mAlertDialog;
    private PlayListAdapter adapter;
    private PlayListUpdateDeleteAdapter otherAdapter;
    private EditText mPlaylistTextName;
    private boolean isContestualMenu = false;

    private DragPlayerFragment mDragPlayerFragment;
    private int mSystemBarHeight;
    private DBHelper mDBHelper;
    private ArrayList<PlayList> mPlayList;

    private RecyclerItemClickListener adapterListener;

    @OnClick(R.id.playlist_fab) void FabOnClick(){
        mAlertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Playlist");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDBHelper = new DBHelper(this);
        mPlayList = mDBHelper.getAllPlayLists();

        if(mPlayList.isEmpty()){
            mEmptyListTextView.setText(empty_list_playlist);
        }else {
            adapter = new PlayListAdapter(this, mPlayList);
            otherAdapter = new PlayListUpdateDeleteAdapter(this, mPlayList);

            rvPlaylists.setAdapter(adapter);
            rvPlaylists.setLayoutManager(new LinearLayoutManager(this));

            adapterListener = new RecyclerItemClickListener(this, rvPlaylists, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(getApplicationContext(), InsidePlaylistActivity.class);
                    intent.putExtra("playlist_id", mPlayList.get(position).getPlayListId());
                    intent.putExtra("playlist_name", mPlayList.get(position).getPlayListName());
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    isContestualMenu = true;
                    changeLayout(isContestualMenu);
                }
            });
            rvPlaylists.addOnItemTouchListener(adapterListener);
        }
        mSystemBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));

        /**
         * Recupera lo stato del fragment del player quando il dispositivo viene girato.
         */
        if(getSupportFragmentManager().findFragmentByTag("DragPlayer") != null)
            mDragPlayerFragment = (DragPlayerFragment)getSupportFragmentManager().findFragmentByTag("DragPlayer");
        else{
            mDragPlayerFragment = new DragPlayerFragment();
            mDragPlayerFragment.setLayoutId(R.id.playlist_fragment_drag_player, R.id.content_playlist);
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.add(R.id.playlist_fragment_drag_player, mDragPlayerFragment, "DragPlayer");
            trans.commit();
        }
        /*************************************************************************/

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
                    Toast.makeText(PlaylistActivity.this, playlist_empty_error, Toast.LENGTH_SHORT).show();
                else{
                    if(!mDBHelper.insertPlaylist(txt)){
                        Toast.makeText(PlaylistActivity.this, playlist_error_message, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(PlaylistActivity.this, playlist_success_message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), TrackPlaylistSelectionActivity.class);
                        intent.putExtra("playlist_id", mDBHelper.getPlaylistID(txt));
                        intent.putExtra("playlist_name", txt);
                        startActivity(intent);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(mDragPlayerContainer.getHeight() > mDragPlayerFragment.getSnackBarHeight())
        {
            //int mResult = this.getWindow().getDecorView().getWidth();
            int mResult = this.getWindow().getDecorView().getWidth()-(mSystemBarHeight);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mDragPlayerContainer.getLayoutParams();
            layoutParams.height = mResult;
            mDragPlayerContainer.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onBackPressed() {
        if(isContestualMenu){
            isContestualMenu = false;
            changeLayout(isContestualMenu);
        }else{
            finish();
        }
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

    private boolean changeLayout(boolean contestual_menu) {
        if(contestual_menu) {
            rvPlaylists.setAdapter(otherAdapter);
            rvPlaylists.setLayoutManager(new LinearLayoutManager(this));
            rvPlaylists.removeOnItemTouchListener(adapterListener);
            return true;
        }else {
            rvPlaylists.setAdapter(adapter);
            rvPlaylists.setLayoutManager(new LinearLayoutManager(this));
            rvPlaylists.addOnItemTouchListener(adapterListener);
            return false;
        }
    }
}
