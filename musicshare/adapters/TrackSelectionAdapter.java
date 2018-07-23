package com.sms.musicshare.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.sms.musicshare.R;
import com.sms.musicshare.TrackPlaylistSelectionActivity;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.Prova;

import java.util.ArrayList;
import java.util.List;

public class TrackSelectionAdapter extends RecyclerView.Adapter<TrackSelectionAdapter.TrackSelectionView>{

    private Context mContext;

    private List<InfoTrack> mTracks;
    private List<Boolean> mItemChecked;
    // Pass in the contact array into the constructor
    public TrackSelectionAdapter(Context context, List<InfoTrack> tracks, List<Boolean> itemChecked) {
        mTracks = tracks;
        mContext = context;
        mItemChecked = itemChecked;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public TrackSelectionAdapter.TrackSelectionView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View trackSelectionView = inflater.inflate(R.layout.track_selection_adapter_row, parent, false);

        // Return a new holder instance
        TrackSelectionAdapter.TrackSelectionView viewHolder = new TrackSelectionAdapter.TrackSelectionView(trackSelectionView, (TrackPlaylistSelectionActivity) mContext);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrackSelectionAdapter.TrackSelectionView holder, int position) {
        // Get the data model based on position
        InfoTrack infoTrack = mTracks.get(position);

        // Set item views based on your views and data model
        holder.mTrack.setText(infoTrack.getTitle());
        holder.mArtist.setText(infoTrack.getArtist().getName());

        if(mItemChecked.get(position))
            holder.mCheckBox.setChecked(true);
        else
            holder.mCheckBox.setChecked(false);

        String imageArtPath = infoTrack.getAlbum().getAlbumArt();
        if (!imageArtPath.equals("<unknown>"))
            holder.mAlbumImage.setImageDrawable(Drawable.createFromPath(imageArtPath));
        else
            holder.mAlbumImage.setImageResource(R.drawable.cd);
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    public void setFilter(List<InfoTrack> param, List<Boolean> bool) {
        mTracks = new ArrayList<>();
        mItemChecked = new ArrayList<>();
        mTracks.addAll(param);
        mItemChecked.addAll(bool);
        notifyDataSetChanged();
    }

    public List<Boolean> getListCheckedItems(){
        return mItemChecked;
    }

    class TrackSelectionView extends RecyclerView.ViewHolder  implements CheckBox.OnClickListener{

        TextView mTrack;
        TextView mArtist;
        ImageView mAlbumImage;
        CheckBox mCheckBox;
        TrackPlaylistSelectionActivity mTrackPlaylistSelectionActivity;

        public TrackSelectionView(View itemView, TrackPlaylistSelectionActivity trackPlaylistSelectionActivity) {
            super(itemView);

            mTrack = (TextView) itemView.findViewById(R.id.id_track_text_into_track_selection_adapter_row);
            mArtist = (TextView) itemView.findViewById(R.id.id_artist_text_into_track_selection_adapter_row);
            mAlbumImage = (ImageView) itemView.findViewById(R.id.id_image_track_selection_adapter_row);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.id_checkBox_into_track_selection_adapter_row);
            mTrackPlaylistSelectionActivity = trackPlaylistSelectionActivity;
            mCheckBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mTrackPlaylistSelectionActivity.prepareSelection(view, getAdapterPosition());
            if(mCheckBox.isChecked())
                mItemChecked.set(getAdapterPosition(), true);
            else
                mItemChecked.set(getAdapterPosition(), false);
        }
    }
}
