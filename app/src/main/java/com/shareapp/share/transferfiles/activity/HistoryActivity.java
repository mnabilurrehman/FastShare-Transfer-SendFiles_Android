package com.shareapp.share.transferfiles.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.adapters.FilesHistoryAdapter;
import com.shareapp.share.transferfiles.transfer.TransferService;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.shareapp.share.transferfiles.widget.ReceivedFiles;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    FilesCart filesCart;
    private ArrayList<File> arrayList;
    private ImageView ivEmptyList;
    ActionBar actionBar;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Download Center");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        arrayList = new ArrayList<>();
        ivEmptyList = (ImageView) findViewById(R.id.ivEmptyList);
        filesCart = new FilesCart(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Passing the column number 1 to show online one column in each row.
        recyclerViewLayoutManager = new GridLayoutManager(this, 1);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        arrayList = new ReceivedFiles().getAllFiles();

        if (arrayList == null || arrayList.size() == 0) {
            ivEmptyList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            ivEmptyList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new FilesHistoryAdapter(this, arrayList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onDestroy() {
        TransferService.startStopService(this, false);
        super.onDestroy();
    }

//    public void bSendClicked(View view) {
//        if (filesCart.getQuantity() == 0) {
//            Toasty.info(getApplicationContext(), "Please select file to send").show();
//        } else {
//            ArrayList<Uri> uriArrayList = new ArrayList<Uri>();
//            for (String path : filesCart.getFileLink()) {
//                Uri uri = Uri.fromFile(new File(path));
//                uriArrayList.add(uri);
//            }
//
//            Intent shareIntent = new Intent(this, ShareActivity.class);
//            shareIntent.setAction("android.intent.action.SEND_MULTIPLE");
//            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList);
//            startActivity(shareIntent);
////        this.startActivityForResult(shareIntent, ReqeustKey.SHARE_FILE_REQUEST);
//        }
//    }
}
