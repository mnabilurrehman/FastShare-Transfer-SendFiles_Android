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

/**
 * Created by Ji Janab on 4/27/2018.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private final static String TAG = "AppLog/TabsPagerAdapter";

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Log.i(TAG, "Item Position:" + position);

        switch (position)
        {
            case 0:
                return new AudioFilesFragment();
            case 1:
                return new AppApkFragment();
            case 2:
                return new VideoFilesFragment();
            case 3:
                return new PhotoFragment();
            case 4:
                return new FilesFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
