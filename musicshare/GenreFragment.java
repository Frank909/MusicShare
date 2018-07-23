package com.sms.musicshare;

import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sms.musicshare.adapters.GenreAdapter;
import com.sms.musicshare.helper.Album;
import com.sms.musicshare.helper.Artist;
import com.sms.musicshare.helper.Genre;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.Prova;
import com.sms.musicshare.helper.RetainInfoFromTracksList;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Iterator;

public class GenreFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<InfoTrack> mTracks = new ArrayList<>();
    private ArrayList<Genre> mGenresList = new ArrayList<>();

    public GenreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GenreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GenreFragment newInstance(String param1, String param2) {
        GenreFragment fragment = new GenreFragment();
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
        if (getArguments().getBoolean("staticInformation"))
            mGenresList.addAll(RetainInfoFromTracksList.getStaticGenres());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_genre, container, false);

        RecyclerView rvGenres = (RecyclerView) v.findViewById(R.id.id_genre_list);

        GenreAdapter adapter = new GenreAdapter(this.getActivity(), mGenresList, mTracks);
        // Attach the adapter to the recyclerview to populate items
        rvGenres.setAdapter(adapter);
        // Set layout manager to position the items
        rvGenres.setLayoutManager(new LinearLayoutManager(this.getActivity()));

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
