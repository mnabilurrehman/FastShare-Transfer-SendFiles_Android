package com.shareapp.share.transferfiles.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.domain.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class VideoAndAdItemListAdapter extends BaseAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    private List<Video> mData = new ArrayList();
    private LayoutInflater mInflater;

    private TreeSet mSeparatorsSet = new TreeSet();

    public VideoAndAdItemListAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void addItem(final Video item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSeparatorItem(final String item) {
//        mData.add(item);
        // save separator position
        mSeparatorsSet.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Video getItem(int position) {

        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ViewHolderAd viewHolderAd = null;
        int type = getItemViewType(position);
        System.out.println("getView " + position + " " + convertView + " type = " + type);
        if (convertView == null) {
            switch (type) {
                case TYPE_ITEM:
                    holder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.list_item_video, null);
                    holder.ivVideo = (ImageView) convertView.findViewById(R.id.ivThumbNail);
                    holder.tvVideoTitle = (TextView) convertView.findViewById(R.id.tvVideoTitle);
                    holder.tvChannelTitle = (TextView) convertView.findViewById(R.id.tvChannelName);
                    holder.ivPlayButton = (ImageView) convertView.findViewById(R.id.ivPlayButton);
                    break;
                case TYPE_SEPARATOR:
                    viewHolderAd = new ViewHolderAd();
                    convertView = mInflater.inflate(R.layout.item_ad_view, null);
                    viewHolderAd .mAdView= convertView.findViewById(R.id.adView);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        holder.tvVideoTitle.setText(mData.get(position));
        return convertView;
    }


    public class ViewHolder {
        private ImageView ivVideo;
        private TextView tvVideoTitle;
        private ImageView ivPlayButton;
        private TextView tvChannelTitle;
    }

    public class ViewHolderAd{
        private AdView mAdView;
    }

}

