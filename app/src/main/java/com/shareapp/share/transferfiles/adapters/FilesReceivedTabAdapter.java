package com.shareapp.share.transferfiles.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.shareapp.share.transferfiles.fragments.AppApkFragment;
import com.shareapp.share.transferfiles.fragments.AudioFilesFragment;
import com.shareapp.share.transferfiles.fragments.FilesFragment;
import com.shareapp.share.transferfiles.fragments.PhotoFragment;
import com.shareapp.share.transferfiles.fragments.VideoFilesFragment;
import com.shareapp.share.transferfiles.fragments.YoutubeVideoFragment;

import java.util.ArrayList;

public class FilesReceivedTabAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> _fragments;

    private final static String TAG = "AppLog/FleRecvedTbAdptr";

    public FilesReceivedTabAdapter(FragmentManager fm) {
        super(fm);

        this._fragments = new ArrayList<Fragment>();
    }

    public void add(Fragment fragment) {
        this._fragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {

        Log.i(TAG, "Item Position:" + position);
//        return this._fragments.get(position);
        switch (position) {
            case 0:
                return new YoutubeVideoFragment();
            case 1:
                return new AudioFilesFragment();
            case 2:
                return new AppApkFragment();
            case 3:
                return new VideoFilesFragment();
            case 4:
                return new PhotoFragment();
            case 5:
                return new FilesFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 6;
    }


}
