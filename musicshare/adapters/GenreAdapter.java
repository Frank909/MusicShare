package com.sms.musicshare.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sms.musicshare.GenreActivity;
import com.sms.musicshare.R;
import com.sms.musicshare.helper.Genre;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.Prova;
import com.sms.musicshare.helper.RetainInfoFromTracksList;

import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreView>  {


    private Context mContext;
    private ArrayList<InfoTrack> mTracks;
    private List<Genre> mGenres;

    // Pass in the contact array into the constructor
    public GenreAdapter(Context context, List<Genre> genres, ArrayList<InfoTrack> mTracks) {
        mGenres = genres;
        mContext = context;
        this.mTracks = mTracks;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public GenreAdapter.GenreView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View genreView = inflater.inflate(R.layout.genre_row, parent, false);

        // Return a new holder instance
        GenreAdapter.GenreView viewHolder = new GenreAdapter.GenreView(genreView, getContext(), mGenres, mTracks);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GenreAdapter.GenreView holder, int position) {
        // Get the data model based on position
        Genre genre = mGenres.get(position);

        // Set item views based on your views and data model
        holder.mGenre.setText(genre.getName());
        holder.mN_Tracks.setText(genre.getNTracks() + " " + getContext().getResources().getQuantityString(R.plurals.number_of_playlist_tracks, Integer.parseInt(genre.getNTracks()), Integer.parseInt(genre.getNTracks())));
    }
    @Override
    public int getItemCount() {
        return mGenres.size();
    }

    class GenreView  extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView mN_Tracks, mGenre;
        private ImageView mAlbumImage;
        private List<Genre> genreList;
        private Context context;
        private ArrayList<InfoTrack> mTracks;

        GenreView(View itemView, Context context, List<Genre> genreList, ArrayList<InfoTrack> mTracks) {
            super(itemView);
            this.genreList = genreList;
            this.context = context;
            this.mTracks = mTracks;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mN_Tracks = (TextView) itemView.findViewById(R.id.id_NTracks_text_into_genre_row);
            mGenre = (TextView) itemView.findViewById(R.id.id_genre_text_into_genre_row);
            mAlbumImage = (ImageView) itemView.findViewById(R.id.id_image_genre_row);
        }

        @Override
        public void onClick(View v) {
            Genre genre = genreList.get(getAdapterPosition());

            ArrayList<InfoTrack> tracksFromGenresList = new RetainInfoFromTracksList(mTracks).retainTracksListFromGenre(genre);

            Intent intent = new Intent(getContext(), GenreActivity.class);
            intent.putExtra("genre_name", genre.getName());
            intent.putParcelableArrayListExtra("tracksFromGenresList", tracksFromGenresList);
            this.context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
