package com.sms.musicshare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sms.musicshare.adapters.ArtistAdapter;
import com.sms.musicshare.helper.Artist;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.RetainInfoFromTracksList;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.util.ArrayList;

public class ArtistFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<InfoTrack> mTrackList = new ArrayList<>();
    private ArrayList<Artist> mArtistsList = new ArrayList<>();
    private RecyclerView rvArtists;

    public ArtistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArtistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArtistFragment newInstance(String param1, String param2) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTrackList.addAll(RetainInfoFromTracksList.getStaticTracks());
        if (getArguments().getBoolean("staticInformation"))
            mArtistsList.addAll(RetainInfoFromTracksList.getStaticArtists());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_artist, container, false);
        rvArtists = (RecyclerView) v.findViewById(R.id.id_artist_list);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        ArtistAdapter adapter = new ArtistAdapter(this.getActivity(), mArtistsList, mTrackList);
        // Attach the adapter to the recyclerview to populate items
        rvArtists.setAdapter(adapter);
        // Set layout manager to position the items
        rvArtists.setLayoutManager(new LinearLayoutManager(this.getActivity()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
