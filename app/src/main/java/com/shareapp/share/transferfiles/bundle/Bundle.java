package com.shareapp.share.transferfiles.bundle;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * List of items to be transferred
 */
public class Bundle extends ArrayList<Item> {

    private final static String TAG = "AppLog/Bundle";

    private long mTotalSize = 0;

    /**
     * Add the specified item to the bundle for transfer
     */
    public void addItem(Item item) throws IOException {
        Log.i(TAG, "Item Added in Bundle");
        add(item);
        mTotalSize += item.getLongProperty(Item.SIZE, true);
    }

    /**
     * Retrieve the total size of the bundle content
     * @return total size in bytes
     */
    public long getTotalSize() {
        Log.i(TAG, "Total side of Bundle: " + mTotalSize);
        return mTotalSize;
    }
}
