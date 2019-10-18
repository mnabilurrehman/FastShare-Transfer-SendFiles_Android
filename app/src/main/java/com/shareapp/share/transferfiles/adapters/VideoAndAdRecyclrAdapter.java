package com.shareapp.share.transferfiles.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.activity.VideoActivity;
import com.shareapp.share.transferfiles.domain.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAndAdRecyclrAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AppLog/VdoNAdRecylrAdap";

    private AdRequest adRequest;
    private List<Video> videoList;

    private Context context;

    public VideoAndAdRecyclrAdapter(List<Video> videoList, Context context) {
//        Log.d(TAG, "VideoAndAdRecyclrAdapter");
        this.videoList = videoList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder " + "View type: " + viewType);
        View v;
        switch (viewType) {
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_video, parent, false);
                return new ViewHolderVideo(v);
            case 2:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_view, parent, false);
                return new ViewHolderAd(v);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            Log.v(TAG, "onBindViewHolder holder Position: " + position);

//        Log.d(TAG, "onBindViewHolder " + "holder.getItemViewType(): " + holder.getItemViewType());
        switch (holder.getItemViewType()) {
            case 1:
                ViewHolderVideo viewHolderVideo = (ViewHolderVideo) holder;
                int n = position;

//                if (n > 5) {
//                    n = n - 2;
//                } else if (n > 3) {
//                    n = n - 1;
//                }

//                if (n % 4 == 0) {
                if (n > 24) {
                    n = n - 7;
                    Log.d(TAG, "Index Number: n > 27");
                } else if (n > 20) {
                    n = n - 6;
                    Log.d(TAG, "Index Number: n > 20");
                } else if (n > 16) {
                    n = n - 5;
                    Log.d(TAG, "Index Number: n > 16");
                } else if (n > 12) {
                    n = n - 4;
                    Log.d(TAG, "Index Number: n > 12");
                } else if (n > 8) {
                    n = n - 3;
                    Log.d(TAG, "Index Number: n > 8");
                } else if (n > 4) {
                    n = n - 2;
                    Log.d(TAG, "Index Number: n > 4");
                } else if (n > 0) {
                    n = n - 1;
                    Log.d(TAG, "Index Number: n > 1");
                }
//                }
                Log.d(TAG, "Index Number: " + n);
                viewHolderVideo.tvChannelTitle.setText(videoList.get(n).getChannelName());
                viewHolderVideo.tvVideoTitle.setText(videoList.get(n).getTitle());
                Picasso.get().load(videoList.get(n).getThumbUrl()).into(viewHolderVideo.ivVideo);
                final int finalN = n;
                viewHolderVideo.ivPlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent nextActivity = new Intent(context, VideoActivity.class);
                        nextActivity.putExtra("key", videoList.get(finalN).getUrl());
                        context.startActivity(nextActivity);
                    }
                });
                break;
            case 2:
                ViewHolderAd viewHolderAd = (ViewHolderAd) holder;
                adRequest = new AdRequest.Builder().build();
//                ((ViewHolderAd) holder).mAdView.loadAd(adRequest);
                viewHolderAd.mAdView.loadAd(adRequest);
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
//        Log.d(TAG, "getItemViewType");
//        return super.getItemViewType(position);

        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
//        return 1;
//        if (position == 3 || position == 5) {
//            return 2;
//        } else {
//            return 1;
//        }

        if (position % 4 == 0) {
            return 2;
        } else {
            return 1;
        }

//        return position % 2 * 2;
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount " + (videoList.size() + 7));
        // return size of video list + number of ads to be shown
        return videoList.size() + 7;
    }

    class ViewHolderVideo extends RecyclerView.ViewHolder {

        private ImageView ivVideo;
        private TextView tvVideoTitle;
        private ImageView ivPlayButton;
        private TextView tvChannelTitle;

        public ViewHolderVideo(View convertView) {
            super(convertView);
            ivVideo = (ImageView) convertView.findViewById(R.id.ivThumbNail);
            tvVideoTitle = (TextView) convertView.findViewById(R.id.tvVideoTitle);
            tvChannelTitle = (TextView) convertView.findViewById(R.id.tvChannelName);
            ivPlayButton = (ImageView) convertView.findViewById(R.id.ivPlayButton);
        }
    }

    class ViewHolderAd extends RecyclerView.ViewHolder {

        private AdView mAdView;

        public ViewHolderAd(View convertView) {
            super(convertView);
            mAdView = convertView.findViewById(R.id.adView);
        }

    }
}