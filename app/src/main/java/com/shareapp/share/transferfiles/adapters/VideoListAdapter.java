package com.shareapp.share.transferfiles.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.domain.Video;
import com.shareapp.share.transferfiles.activity.VideoActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Nabil ur Rehman on 5/1/2018.
 */

public class VideoListAdapter extends BaseAdapter {

    private final static String TAG = "AppLog/VideoListAdapter";

    List<Video> videos;
    private LayoutInflater mInflater;
    private Context context;

    private ImageView ivVideo;
    private TextView tvVideoTitle;
    private ImageView ivPlayButton;
    private TextView tvChannelTitle;

    public VideoListAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
        this.mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int i) {
        return videos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = mInflater.inflate(R.layout.list_item_video, null);
            Log.i(TAG, "view is null");
        }

        if (i == 5){

        }

        final Video video = videos.get(i);

        ivVideo = (ImageView) view.findViewById(R.id.ivThumbNail);
        tvVideoTitle = (TextView) view.findViewById(R.id.tvVideoTitle);
        tvChannelTitle = (TextView) view.findViewById(R.id.tvChannelName);
        ivPlayButton = (ImageView) view.findViewById(R.id.ivPlayButton);
        ivPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextActivity = new Intent(context, VideoActivity.class);
                nextActivity.putExtra("key", video.getUrl());
                context.startActivity(nextActivity);
            }
        });

        tvChannelTitle.setText(video.getChannelName());


        String link = video.getThumbUrl();
        Log.i(TAG, i + link);
        Picasso.get().load(link).into(ivVideo);

        tvVideoTitle.setText(video.getTitle());

        return view;
    }

}
