package com.shareapp.share.transferfiles.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.transfer.TransferService;

public class Activity_galleryview extends AppCompatActivity {

    String str_video;
    VideoView vv_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galleryview);
        init();
    }

    private void init() {

        vv_video = (VideoView) findViewById(R.id.vv_video);

        str_video = getIntent().getStringExtra("video");
        vv_video.setVideoPath(str_video);
        vv_video.start();

    }

    @Override
    protected void onDestroy() {
        TransferService.startStopService(this, false);
        super.onDestroy();
    }
}
