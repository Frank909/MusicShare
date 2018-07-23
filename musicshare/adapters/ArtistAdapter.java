package com.sms.musicshare.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sms.musicshare.ArtistActivity;
import com.sms.musicshare.R;
import com.sms.musicshare.helper.Artist;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.RetainInfoFromTracksList;

import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistView>  {
    private Context mContext;
    private ArrayList<InfoTrack> mTracks;
    private List<Artist> mArtists;

    // Pass in the contact array into the constructor
    public ArtistAdapter(Context context, List<Artist> mArtists, ArrayList<InfoTrack> mTracks) {
        this.mArtists = mArtists;
        this.mContext = context;
        this.mTracks = mTracks;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public ArtistAdapter.ArtistView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View artistView = inflater.inflate(R.layout.artist_row, parent, false);

        // Return a new holder instance
        ArtistAdapter.ArtistView viewHolder = new ArtistAdapter.ArtistView(artistView, getContext(), mTracks);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArtistAdapter.ArtistView holder, int position) {
        // Get the data model based on position
        Artist artist = mArtists.get(position);

        // Set item views based on your views and data model
        holder.mArtist.setText(artist.getName());

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(sp.getBoolean("pref_show_count_albums_tracks", true)) {
            holder.mN_Albums.setText(artist.getCountAlbums() + " " + getContext().getResources().getQuantityString(R.plurals.number_of_playlist_albums, Integer.parseInt(artist.getCountAlbums()), Integer.parseInt(artist.getCountAlbums())));
            holder.mN_Tracks.setText(artist.getCountTracks() + " " + getContext().getResources().getQuantityString(R.plurals.number_of_playlist_tracks, Integer.parseInt(artist.getCountTracks()), Integer.parseInt(artist.getCountTracks())));
        }
    }
    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    public void setFilter(List<Artist> param) {
        mArtists = new ArrayList<>();
        mArtists.addAll(param);
        notifyDataSetChanged();
    }

    public List<Artist> getListArtists(){ return mArtists; }

    class ArtistView extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private ImageView mArtistImage;
        private TextView mArtist, mN_Albums, mN_Tracks;
        private Context context;
        private ArrayList<InfoTrack> mTracks;

        ArtistView(View itemView, Context context, ArrayList<InfoTrack> mTracks) {
            super(itemView);
            this.context = context;
            this.mTracks = mTracks;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mArtistImage = (ImageView) itemView.findViewById(R.id.id_image_artist_row);
            mArtist = (TextView) itemView.findViewById(R.id.id_text_artist_into_artist_row);
            mN_Albums = (TextView) itemView.findViewById(R.id.id_nAlbum_into_artist_row);
            mN_Tracks = (TextView) itemView.findViewById(R.id.id_nTraks_into_artist_row);
        }


        @Override
        public void onClick(View v) {
            Artist artist = getListArtists().get(getAdapterPosition());

            ArrayList<InfoTrack> infoTracks =  new RetainInfoFromTracksList(mTracks).retainTracksListFromArtist(artist);

            Intent intent = new Intent(getContext(), ArtistActivity.class);
            intent.putParcelableArrayListExtra("tracks_list_from_artist", infoTracks);
            intent.putExtra("artist", artist);

            this.context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
