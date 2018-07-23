package com.sms.musicshare.adapters;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.AlbumActivity;
import com.sms.musicshare.FavoritesActivity;
import com.sms.musicshare.InsidePlaylistActivity;
import com.sms.musicshare.PlaylistActivity;
import com.sms.musicshare.R;
import com.sms.musicshare.SearchActivity;
import com.sms.musicshare.helper.Artist;
import com.sms.musicshare.helper.ContextualMenu;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.MusicBackGroundService;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackView>{

    private Context mContext;
    private ArrayList<InfoTrack> mTrackList;
    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    // Pass in the contact array into the constructor
    public TrackAdapter(Context context, ArrayList<InfoTrack> tracks) {
        mTrackList = tracks;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public TrackView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View trackView = inflater.inflate(R.layout.track_row, parent, false);

        // Return a new holder instance
        TrackView viewHolder = new TrackView(trackView, getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TrackView holder, int position) {
        // Get the data model based on position
        InfoTrack track = mTrackList.get(position);

        // Set item views based on your views and data model
        holder.mTrack.setText(track.getTitle());
        holder.mArtist.setText(track.getArtist().getName());

        if(!(getContext() instanceof AlbumActivity)) {
            String imageArtPath = track.getAlbum().getAlbumArt();
            if (!imageArtPath.equals("<unknown>"))
                holder.mAlbumImage.setImageDrawable(Drawable.createFromPath(imageArtPath));
            else
                holder.mAlbumImage.setImageResource(R.drawable.cd);
        }else
            holder.mAlbumImage.setVisibility(View.GONE);
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

    class TrackView extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView mTrack, mArtist;
        private ImageView mAlbumImage, mMenu;
        private Context context;
        private ContextualMenu contextualMenu;

        TrackView(View itemView, Context context) {
            super(itemView);
            this.context = context;

            this.contextualMenu = new ContextualMenu(this.context);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mTrack = (TextView) itemView.findViewById(R.id.id_track_text_into_track_row);
            mArtist = (TextView) itemView.findViewById(R.id.id_artist_text_into_track_row);
            mAlbumImage = (ImageView) itemView.findViewById(R.id.id_image_track_row);
            mMenu = (ImageView) itemView.findViewById(R.id.id_image_menu_into_track_row);

            if(!(getContext() instanceof FavoritesActivity) &&
                    !(getContext() instanceof PlaylistActivity) &&
                    !(getContext() instanceof InsidePlaylistActivity)) {
                mMenu.setOnClickListener(this);
                mMenu.setVisibility(View.VISIBLE);
            }else
                mMenu.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.id_image_menu_into_track_row){
                this.contextualMenu.createContextualMenu(getListTracks().get(getAdapterPosition()));
            }else{
                Intent intent = new Intent(getContext(), MusicBackGroundService.class);
                intent.setAction(MusicBackGroundService.ACTION_PLAY);
                int pos = getAdapterPosition();
                intent.putExtra("track", (Parcelable) getListTracks().get(pos));
                intent.putExtra("index", pos);
                getContext().startService(intent);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            this.contextualMenu.createContextualMenu(getListTracks().get(getAdapterPosition()));
            return true;
        }
    }
}
