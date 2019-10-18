package com.shareapp.share.transferfiles.widget;

import android.content.Context;

import java.util.ArrayList;

public class FilesCart {

    private final static String TAG = "FilesCart";

    private Context context;

    private static ArrayList<String> fileLink;

    public FilesCart(Context context) {
        if (fileLink == null) {
            fileLink = new ArrayList<String>();
        }
        this.context = context;
    }

    public boolean addFile(String link) {
        if (!fileLink.contains(link)) {
            fileLink.add(link);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeFile(String link) {
        if (fileLink.contains(link)) {
            fileLink.remove(link);
            return true;
        } else {
            return false;
        }
    }

    public int getQuantity() {
        return fileLink.size();
    }

    public ArrayList<String> getFileLink() {
        return fileLink;
    }

    public void destroyCart() {
        fileLink = null;
    }

    public boolean isPresent(String link) {
        if (fileLink.contains(link)) {
            return true;
        }
        return false;
    }

}
