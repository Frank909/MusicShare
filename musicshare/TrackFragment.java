package com.sms.musicshare;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.animation.ResizeAnimation;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.helper.ContextualMenu;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.OnContextualMenuListener;
import com.sms.musicshare.helper.Prova;
import com.sms.musicshare.adapters.TrackAdapter;
import com.sms.musicshare.helper.OnStartDragListener;
import com.sms.musicshare.helper.RetainInfoFromTracksList;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrackFragment extends Fragment  implements OnStartDragListener/*, OnContextualMenuListener */{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindString(R.string.text_favorites_popup_menu)  String string_addFavorites;
    @BindString(R.string.text_favorites_popup_menu_remove)  String string_removeFavorites;
    @BindString(R.string.dialog_track_title)  String string_dialogTrackTitle;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<InfoTrack> mTracks = new ArrayList<>();
    private DBHelper mDBHelper;
    private RecyclerView rvMusic;
    private View v;

    public TrackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrackFragment newInstance(String param1, String param2) {
        TrackFragment fragment = new TrackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this, getActivity());

        if (getArguments().getBoolean("staticInformation"))
            mTracks = RetainInfoFromTracksList.getStaticTracks();
        else if (getArguments().getParcelableArrayList("tracks_artist_list") != null)
            mTracks = getArguments().getParcelableArrayList("tracks_artist_list");
        else if(getActivity().getIntent().getExtras().getString("album_name") != null){
            mTracks = getActivity().getIntent().getParcelableArrayListExtra("tracksFromAlbumsList");
        }

        mDBHelper = new DBHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_track, container, false);

        rvMusic = (RecyclerView) v.findViewById(R.id.id_track_list);
        rvMusic.setHasFixedSize(true);

        TrackAdapter adapter = new TrackAdapter(this.getActivity(), mTracks);
        // Attach the adapter to the recyclerview to populate items
        rvMusic.setAdapter(adapter);
        // Set layout manager to position the items
        rvMusic.setLayoutManager(new LinearLayoutManager(this.getActivity()));

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
    }

}
