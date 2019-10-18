package com.shareapp.share.transferfiles.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.adapters.GrpAudioListAdapter;
import com.shareapp.share.transferfiles.util.AudioFileExtractor;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GrpAudioFilesFragment extends Fragment {
    private final static String TAG = "AppLog/GrpAudioFlesFrag";
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    private AdView mAdView;

    public GrpAudioFilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grp_audio_files, container, false);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // Passing the column number 1 to show online one column in each row.
        recyclerViewLayoutManager = new GridLayoutManager(getContext(), 1);

        try {

            recyclerView.setLayoutManager(recyclerViewLayoutManager);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        adapter = new GrpAudioListAdapter(getContext(), new AudioFileExtractor().getAllAudioFromDevice(getContext()));

        recyclerView.setAdapter(adapter);

        return view;
    }

}
