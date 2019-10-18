package com.shareapp.share.transferfiles.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.adapters.AudioListAdapter;
import com.shareapp.share.transferfiles.util.AudioFileExtractor;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class AudioFilesFragment extends Fragment {

    private static final String TAG = "AppLog/AudioFilesFrag";

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    private AdView mAdView;


//    private InterstitialAd mInterstitialAd;

    public AudioFilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

//        mInterstitialAd = new InterstitialAd(getContext());
//        mInterstitialAd.setAdUnitId(getString(R.string.sample_ad_interstitial));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        mInterstitialAd.setAdListener(new AdListener(){
//            @Override
//            public void onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//                Log.d(TAG,"onAdLoaded()");
//                showInterstitialAd();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                // Code to be executed when an ad request fails.
//                Log.d(TAG,"onAdFailedToLoad" + errorCode);
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when the ad is displayed.
//                Log.d(TAG,"onAdOpened()");
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//                Log.d(TAG,"onAdLeftApplication()");
//            }
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when when the interstitial ad is closed.
//                Log.d(TAG,"onAdClosed()");
//            }
//        });

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_files, container, false);


        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // Passing the column number 1 to show online one column in each row.
        recyclerViewLayoutManager = new GridLayoutManager(getContext(), 1);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        adapter = new AudioListAdapter(getContext(), new AudioFileExtractor().getAllAudioFromDevice(getContext()));

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

//    private void showInterstitialAd() {
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        } else {
//            Log.d("TAG", "The interstitial wasn't loaded yet.");
//        }
//    }
}