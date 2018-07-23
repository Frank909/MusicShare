package com.sms.musicshare.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.text.IDNA;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sms.musicshare.AlbumActivity;
import com.sms.musicshare.R;
import com.sms.musicshare.helper.Album;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.Prova;
import com.sms.musicshare.helper.RetainInfoFromTracksList;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumView>  {
    private Context mContext;
    private List<Album> mAlbumList;
    private ArrayList<InfoTrack> mTracks;

    // Pass in the contact array into the constructor
    public AlbumAdapter(Context context, List<Album> albumList, ArrayList<InfoTrack> mTracks) {
        mAlbumList = albumList;
        mContext = context;
        this.mTracks = mTracks;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public AlbumAdapter.AlbumView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View albumView = inflater.inflate(R.layout.album_row, parent, false);

        // Return a new holder instance
        AlbumAdapter.AlbumView viewHolder = new AlbumAdapter.AlbumView(albumView, mAlbumList, mTracks, getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.AlbumView holder, int position) {
        // Get the data model based on position
        Album album = mAlbumList.get(position);

        // Set item views based on your views and data model
        holder.mN_Albums.setText(album.getName());
        holder.mN_Artists.setText(album.getArtist().getName());

        String imageArtPath = album.getAlbumArt();
        if(!imageArtPath.equals("<unknown>"))
            holder.mAlbumImage.setImageDrawable(Drawable.createFromPath(imageArtPath));
        else
            holder.mAlbumImage.setImageResource(R.drawable.cd);
    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

    class AlbumView extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private ImageView mAlbumImage;
        private TextView mN_Albums, mN_Artists;
        private Context context;
        private List<Album> albumList;
        private ArrayList<InfoTrack> mTracks;

        AlbumView(View itemView, List<Album> albumList, ArrayList<InfoTrack> mTracks, Context context) {
            super(itemView);
            this.context = context;
            this.albumList = albumList;
            this.mTracks = mTracks;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mAlbumImage = (ImageView) itemView.findViewById(R.id.id_image_album_row);
            mN_Artists = (TextView) itemView.findViewById(R.id.id_artist_text_into_album_row);
            mN_Albums = (TextView) itemView.findViewById(R.id.id_album_text_into_album_row);
        }


        @Override
        public void onClick(View v) {
            Album album = this.albumList.get(getAdapterPosition());
            ArrayList<InfoTrack> tracksFromAlbumsList = new RetainInfoFromTracksList(mTracks).retainTracksListFromAlbum(album);

            Intent intent = new Intent(getContext(), AlbumActivity.class);
            intent.putExtra("album_name", album.getName());
            intent.putParcelableArrayListExtra("tracksFromAlbumsList", tracksFromAlbumsList);
            this.context.startActivity(intent);
        }


        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}

