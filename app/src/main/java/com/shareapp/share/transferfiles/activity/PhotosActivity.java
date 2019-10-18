package com.shareapp.share.transferfiles.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.adapters.ImagesAdapter;
import com.shareapp.share.transferfiles.fragments.PhotoFragment;
import com.shareapp.share.transferfiles.transfer.TransferService;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class PhotosActivity extends AppCompatActivity {

    int int_position;
    private GridView gridView;
    ImagesAdapter adapter;
    private FilesCart filesCart;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        gridView = (GridView) findViewById(R.id.gv_folder);
        int_position = getIntent().getIntExtra("value", 1);
        adapter = new ImagesAdapter(this, PhotoFragment.al_images, int_position);

        filesCart = new FilesCart(this);

        gridView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        TransferService.startStopService(this, false);
        super.onDestroy();
    }

    public void bSendClicked(View view) {
        if (filesCart.getQuantity() == 0) {
            Toasty.info(getApplicationContext(), "Please select file to send");
        } else {

            ArrayList<Uri> uriArrayList = new ArrayList<Uri>();
            for (String path : filesCart.getFileLink()) {
                Uri uri = Uri.fromFile(new File(path));
                uriArrayList.add(uri);
            }

            Intent shareIntent = new Intent(this, ShareActivity.class);
            shareIntent.setAction("android.intent.action.SEND_MULTIPLE");
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList);
            startActivity(shareIntent);
        }
    }
}
