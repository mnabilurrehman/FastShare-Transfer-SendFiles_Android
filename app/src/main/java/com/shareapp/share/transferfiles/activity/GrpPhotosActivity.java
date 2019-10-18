package com.shareapp.share.transferfiles.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.adapters.GrpImagesAdapter;
import com.shareapp.share.transferfiles.fragments.GrpPhotoFragment;
import com.shareapp.share.transferfiles.transfer.TransferService;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class GrpPhotosActivity extends AppCompatActivity {

    private static final String TAG = "AppLog/GrpPhotosActiv";

    int int_position;
    private GridView gridView;
    GrpImagesAdapter adapter;
    private FilesCart filesCart;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grp_photos);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        gridView = (GridView) findViewById(R.id.gv_folder);
        int_position = getIntent().getIntExtra("value", 0);

        adapter = new GrpImagesAdapter(this, GrpPhotoFragment.al_images, int_position);

        filesCart = new FilesCart(this);

        gridView.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        TransferService.startStopService(this, false);
        super.onDestroy();
    }
}
