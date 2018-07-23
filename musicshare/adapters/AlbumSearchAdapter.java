package com.sms.musicshare.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sms.musicshare.AlbumActivity;
import com.sms.musicshare.R;
import com.sms.musicshare.SearchActivity;
import com.sms.musicshare.helper.Album;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.Prova;
import com.sms.musicshare.helper.RetainInfoFromTracksList;

import java.util.ArrayList;
import java.util.List;

public class AlbumSearchAdapter extends RecyclerView.Adapter<AlbumSearchAdapter.AlbumSearchView>  {

    private Context mContext;
    private List<Album> mAlbums;

    // Pass in the contact array into the constructor
    public AlbumSearchAdapter(Context context, List<Album> albums) {
        mAlbums = albums;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public AlbumSearchAdapter.AlbumSearchView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View albumSearchView = inflater.inflate(R.layout.album_search_row, parent, false);

        // Return a new holder instance
        AlbumSearchAdapter.AlbumSearchView viewHolder = new AlbumSearchAdapter.AlbumSearchView(albumSearchView, getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AlbumSearchAdapter.AlbumSearchView holder, int position) {
        // Get the data model based on position
        Album album = mAlbums.get(position);

        // Set item views based on your views and data model
        holder.mAlbum.setText(album.getName());
        holder.mArtist.setText(album.getArtist().getName());

        String imageArtPath = album.getAlbumArt();
        if(!imageArtPath.equals("<unknown>"))
            holder.mAlbumImage.setImageDrawable(Drawable.createFromPath(imageArtPath));
        else
            holder.mAlbumImage.setImageResource(R.drawable.cd);
    }
    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    public void setFilter(List<Album> param) {
        mAlbums = new ArrayList<>();
        mAlbums.addAll(param);
        notifyDataSetChanged();
    }

    public List<Album> getListAlbums(){ return mAlbums; }

    class AlbumSearchView extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView mAlbum, mArtist;
        private ImageView mAlbumImage;
        private Context context;

        AlbumSearchView(View itemView, Context context) {
            super(itemView);
            this.context = context;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mAlbum = (TextView) itemView.findViewById(R.id.id_album_text_into_album_search_row);
            mArtist = (TextView) itemView.findViewById(R.id.id_artist_text_into_album_search_row);
            mAlbumImage = (ImageView) itemView.findViewById(R.id.id_image_album_search_row);
        }

        @Override
        public void onClick(View v) {
            Album album = getListAlbums().get(getAdapterPosition());
            ArrayList<InfoTrack> tracksFromAlbumsList = new RetainInfoFromTracksList(RetainInfoFromTracksList.getStaticTracks()).retainTracksListFromAlbum(album);
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