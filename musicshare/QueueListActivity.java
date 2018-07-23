package com.sms.musicshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

import com.sms.musicshare.adapters.QueuePlaylistAdapter;
import com.sms.musicshare.adapters.TrackSelectionAdapter;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.OnStartDragListener;
import com.sms.musicshare.helper.SimpleItemTouchHelperCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QueueListActivity extends AppCompatActivity implements OnStartDragListener{

    @BindView(R.id.id_toolbar_queue_playlist) Toolbar mToolBar;

    private ArrayList<InfoTrack> mTrackList = new ArrayList<>();
    private QueuePlaylistAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.Callback callback;
    private static OnCloseQueueListListener onCloseQueueListListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_list);

        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTrackList.addAll(getIntent().<InfoTrack>getParcelableArrayListExtra("queue"));

        final RecyclerView rvQueue = (RecyclerView) findViewById(R.id.id_queue_playlist_list);

        adapter = new QueuePlaylistAdapter(this, mTrackList, this);

        rvQueue.setAdapter(adapter);
        rvQueue.setLayoutManager(new LinearLayoutManager(this));

        callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvQueue);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public static void setOnCloseQueueListListener(OnCloseQueueListListener L){
        onCloseQueueListListener = L;
    }

    @Override
    public void onBackPressed() {
        finish();
        onCloseQueueListListener.onCloseQueueList(mTrackList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    interface OnCloseQueueListListener{
        void onCloseQueueList(ArrayList<InfoTrack> arrayList);
    }
}
