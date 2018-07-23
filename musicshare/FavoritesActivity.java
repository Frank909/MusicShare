package com.sms.musicshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.adapters.TrackAdapter;
import com.sms.musicshare.adapters.TrackDragSelectAllAdapter;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.database.JSONHelper;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.OnCheckBoxClickListener;
import com.sms.musicshare.helper.OnStartDragListener;
import com.sms.musicshare.helper.RetainInfoFromTracksList;
import com.sms.musicshare.helper.SimpleItemTouchHelperCallback;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FavoritesActivity extends AppCompatActivity implements OnStartDragListener, OnCheckBoxClickListener {

    private ArrayList<InfoTrack> mTrackList = new ArrayList<>();
    private ArrayList<Boolean> mItemsChecked = new ArrayList<>();
    private int default_visibility, mSystemBarHeight;
    private DragPlayerFragment mDragPlayerFragment;
    private TrackAdapter adapter;
    private TrackDragSelectAllAdapter otherAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.Callback callback;
    private boolean isContextualMenu = false, isSelectedAll = false, layoutChanged = false;
    private DBHelper mDBHelper;

    @BindView(R.id.favorites_fab) FloatingActionButton fab;
    @BindView(R.id.favorites_toolbar) Toolbar toolbar;
    @BindView(R.id.favorites_fragment_drag_player) FrameLayout mDragPlayerContainer;
    @BindView(R.id.id_favorites_list) RecyclerView rvFavorites;
    @BindView(R.id.id_favorites_empty_textview) TextView mEmptyListTextView;

    @BindString(R.string.no_favorite_songs) String empty_favorites;
    @BindString(R.string.message_dialog_title) String dialog_title;
    @BindString(R.string.delete_tracks_message) String dialog_msg;
    @BindString(R.string.no_track_selected) String msg_no_selected;


    @OnClick(R.id.favorites_fab) void onFabClick(View view){
        JSONHelper mJSONHelper = new JSONHelper();
        mJSONHelper.createJSONObject(mTrackList);

        Intent intent = new Intent(getApplicationContext(), TrackPlaylistSelectionActivity.class);
        intent.putExtra("favorites_tracks", mJSONHelper.returnJSONObject().toString());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        ButterKnife.bind(this);
        this.default_visibility = fab.getVisibility();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_favorites);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();


        mDBHelper = new DBHelper(this);
        mTrackList.clear();
        mTrackList.addAll(mDBHelper.getAllFavoritesTracks());

        if(mTrackList.isEmpty()){
            mEmptyListTextView.setText(empty_favorites);
            if(adapter != null)
                adapter.notifyDataSetChanged();
        }else {
            adapter = new TrackAdapter(this, mTrackList);
            otherAdapter = new TrackDragSelectAllAdapter(this, mTrackList, mItemsChecked, this, this); //secondo costruttore
            otherAdapter.initCheckboxes();

            rvFavorites.setAdapter(adapter);
            rvFavorites.setLayoutManager(new LinearLayoutManager(this));

            callback = new SimpleItemTouchHelperCallback(otherAdapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(rvFavorites);

            rvFavorites.addOnItemTouchListener(new RecyclerItemClickListener(this, rvFavorites, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    isContextualMenu = true;
                    if (isContextualMenu && !layoutChanged)
                        layoutChanged = changeLayout(isContextualMenu);
                    else
                        layoutChanged = false;
                }
            }));
        }

        mSystemBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));

        /**
         * Recupera lo stato del fragment del player quando il dispositivo viene girato.
         */
        if(getSupportFragmentManager().findFragmentByTag("DragPlayer") != null)
            mDragPlayerFragment = (DragPlayerFragment)getSupportFragmentManager().findFragmentByTag("DragPlayer");
        else{
            mDragPlayerFragment = new DragPlayerFragment();
            mDragPlayerFragment.setLayoutId(R.id.favorites_fragment_drag_player, R.id.content_inside_playlist);
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.add(R.id.favorites_fragment_drag_player, mDragPlayerFragment, "DragPlayer");
            trans.commit();
        }
        /*************************************************************************/
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

        if(isContextualMenu){
            isContextualMenu = false;
            if (!isContextualMenu || layoutChanged)
                layoutChanged = changeLayout(isContextualMenu);
            else
                layoutChanged = true;

        }else
            finish();

    }

    private boolean changeLayout(boolean contestual_menu) {
        toolbar.getMenu().clear();
        if(contestual_menu) {
            fab.setVisibility(View.GONE);
            toolbar.inflateMenu(R.menu.selectall_delete_moveitem);
            rvFavorites.setAdapter(otherAdapter); //mItemChecked Vuoto
            rvFavorites.setLayoutManager(new LinearLayoutManager(this));

            return true;
        }else {
            fab.setVisibility(this.default_visibility);
            rvFavorites.setAdapter(adapter);
            rvFavorites.setLayoutManager(new LinearLayoutManager(this));
            return false;
        }
    }

    @Override
    public void OnCheckedCheckbox(View view) {
        boolean flag = true;

        for(int i=0; i<mItemsChecked.size(); i++)
            if(!mItemsChecked.get(i)){
                toolbar.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unchecked));
                isSelectedAll = false;
                flag = false;
                break;
            }
        if(flag){
            toolbar.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_checked));
            isSelectedAll = true;
        }
    }

    @Override
    public void OnUncheckedCheckbox(View view) {
        toolbar.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unchecked));
        isSelectedAll = false;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;

            case R.id.item_delete:

                new AlertDialog.Builder(this)
                        .setTitle(dialog_title)
                        .setMessage(dialog_msg)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                ArrayList<InfoTrack> tempTrackList = new ArrayList<>();
                                ArrayList<Boolean> tempBoolean = new ArrayList<>();
                                mTrackList = (ArrayList<InfoTrack>) otherAdapter.getListTracks();
                                mItemsChecked = (ArrayList<Boolean>) otherAdapter.getListCheckedItems();
                                tempTrackList.addAll(mTrackList);
                                tempBoolean.addAll(mItemsChecked);
                                boolean flag = false;
                                int i = 0, j = 0;

                                if(mTrackList.size() != 0){
                                    for(boolean element : mItemsChecked){
                                        if(element){
                                            flag = true;
                                            mDBHelper.removeTrackFromFavorites(mTrackList.get(i).getPath());
                                            for(InfoTrack track : RetainInfoFromTracksList.getStaticTracks())
                                                if(track.getPath().equals(mTrackList.get(i).getPath()))
                                                    track.setFavorite(false);
                                            tempTrackList.remove(i - j);
                                            tempBoolean.remove(i - j);
                                            j++;
                                        }
                                        i++;
                                    }
                                    mTrackList.clear();
                                    mTrackList.addAll(tempTrackList);
                                    mItemsChecked.clear();
                                    mItemsChecked.addAll(tempBoolean);
                                    otherAdapter.notifyDataSetChanged();
                                }

                                if(mTrackList.size() == 0) {
                                    mEmptyListTextView.setText(empty_favorites);
                                    toolbar.getMenu().clear();
                                    fab.setVisibility(View.VISIBLE);
                                    rvFavorites.setAdapter(adapter);
                                    rvFavorites.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    isContextualMenu = false;
                                }

                                if(!flag)
                                    Toast.makeText(getApplicationContext(), msg_no_selected , Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                break;

            case R.id.item_select_all:
                if(!isSelectedAll){
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_checked));
                    isSelectedAll = true;
                    otherAdapter.setChecked();
                    otherAdapter.notifyDataSetChanged();
                }else{
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unchecked));
                    isSelectedAll = false;
                    otherAdapter.setUnchecked();
                    otherAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.item_done:
                isContextualMenu = false;
                changeLayout(isContextualMenu);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
