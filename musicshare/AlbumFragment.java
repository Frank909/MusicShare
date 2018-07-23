package com.sms.musicshare;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sms.musicshare.adapters.AlbumAdapter;
import com.sms.musicshare.helper.Album;
import com.sms.musicshare.helper.Artist;
import com.sms.musicshare.helper.Genre;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.MusicLibraryScanner;
import com.sms.musicshare.helper.Prova;
import com.sms.musicshare.helper.RetainInfoFromTracksList;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Iterator;

public class AlbumFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<InfoTrack> mTracks = new ArrayList<>();
    private ArrayList<Album> mAlbumList = new ArrayList<>();

    private RecyclerView rvAlbums;
    int orientation;

    public AlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumFragment newInstance(String param1, String param2) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracks.addAll(RetainInfoFromTracksList.getStaticTracks());
        if (getArguments().getBoolean("staticInformation")) {
            mAlbumList.addAll(RetainInfoFromTracksList.getStaticAlbums());
        }
        else {
            if (getArguments().getParcelableArrayList("album_artist_list") != null)
                mAlbumList = getArguments().getParcelableArrayList("album_artist_list");
            if (getArguments().getParcelableArrayList("tracks_artist_list") != null)
                mTracks = getArguments().getParcelableArrayList("tracks_artist_list");
        }
        orientation = getActivity().getResources().getConfiguration().orientation;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvAlbums.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
            orientation = 1;
        }
        else{
            rvAlbums.setLayoutManager(new GridLayoutManager(this.getActivity(), 5));
            orientation = 2;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_album, container, false);

        rvAlbums = (RecyclerView) v.findViewById(R.id.id_album_list);

        AlbumAdapter adapter = new AlbumAdapter(this.getActivity(), mAlbumList, mTracks);
        // Attach the adapter to the recyclerview to populate items
        rvAlbums.setAdapter(adapter);
        // Set layout manager to position the items
        if(orientation == 1)
            rvAlbums.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
        else if(orientation == 2)
            rvAlbums.setLayoutManager(new GridLayoutManager(this.getActivity(), 5));

        return v;
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
