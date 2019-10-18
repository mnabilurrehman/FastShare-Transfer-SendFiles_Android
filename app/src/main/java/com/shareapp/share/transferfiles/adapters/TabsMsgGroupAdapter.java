package com.shareapp.share.transferfiles.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.shareapp.share.transferfiles.fragments.GrpAppApkFragment;
import com.shareapp.share.transferfiles.fragments.GrpAudioFilesFragment;
import com.shareapp.share.transferfiles.fragments.GrpFilesFragment;
import com.shareapp.share.transferfiles.fragments.GrpPhotoFragment;
import com.shareapp.share.transferfiles.fragments.GrpVideoFilesFragment;

public class TabsMsgGroupAdapter extends FragmentPagerAdapter {

    private final static String TAG = "AppLog/TabsPagerAdapter";

    public TabsMsgGroupAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Log.i(TAG, "Item Position:" + position);

        switch (position)
        {
            case 0:
                return new GrpAudioFilesFragment();
            case 1:
                return new GrpAppApkFragment();
            case 2:
                return new GrpVideoFilesFragment();
            case 3:
                return new GrpPhotoFragment();
            case 4:
                return new GrpFilesFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
