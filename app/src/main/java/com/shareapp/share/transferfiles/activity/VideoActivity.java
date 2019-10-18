package com.shareapp.share.transferfiles.activity;

import android.os.Bundle;
import android.util.Log;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.transfer.TransferService;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class VideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private final static String TAG = "AppLog/VideoActivity";

    private YouTubePlayerView playerView;
    private String youtubeVideoKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        youtubeVideoKey = getIntent().getStringExtra("key");
        Log.i(TAG, "Key: " + youtubeVideoKey);

        playerView = (YouTubePlayerView) findViewById(R.id.player_view);
        playerView.initialize("AIzaSyCNTulSWUQGwhpAszY3eEzLD642ocITLTw", this);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

        if (!b) {
            youTubePlayer.cueVideo(youtubeVideoKey);
        }

    }

    @Override
    protected void onDestroy() {
        TransferService.startStopService(this, false);
        super.onDestroy();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//        Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_LONG).show();
    }
}
