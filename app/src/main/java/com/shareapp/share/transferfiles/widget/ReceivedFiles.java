package com.shareapp.share.transferfiles.widget;

import android.os.Environment;
import android.util.Log;

import com.shareapp.share.transferfiles.config.FileFormat;

import java.io.File;
import java.util.ArrayList;

public class ReceivedFiles {

    private static final String TAG = "AppLog/ReceivedFiles";

    File[] files;

    String[] videoFormat = {".mp4", ".3gp", ".mkv", ".webm"};
    String[] audioFormat = {" .mp3", ".ogg", ".wav"};
    String[] imageFormat = {".jpg", ".gif", ".png", ".bmp", ".webp"};

    public ReceivedFiles() {
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/ShareData";
        File root = Environment.getExternalStorageDirectory();
        File download = new File(root, "Download");
        String path = new File(download, "ShareData").getAbsolutePath();
        Log.d(TAG, "Path: " + path);
        File directory = new File(path);
        files = directory.listFiles();
//        Log.d("Files", "Size: " + files.length);
    }

    public String fileType(String path) {
        for (String s : videoFormat)
            if (path.contains(s)) {
                return FileFormat.video;
            }

        for (String s : audioFormat)
            if (path.contains(s)) {
                return FileFormat.audio;
            }

        for (String s : imageFormat)
            if (path.contains(s)) {
                return FileFormat.image;
            }

        return FileFormat.other;
    }

    public ArrayList<File> getAudioFiles() {
        ArrayList<File> fileArrayList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            if (fileType(files[i].getName().toLowerCase()).contains(FileFormat.audio)) {
                fileArrayList.add(files[i]);
            }
        }
        return fileArrayList;
    }

    public ArrayList<File> getVideoFiles() {
        ArrayList<File> fileArrayList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            if (fileType(files[i].getName().toLowerCase()).contains(FileFormat.video)) {
                fileArrayList.add(files[i]);
            }
        }
        return fileArrayList;
    }

    public ArrayList<File> getAllFiles() {
        ArrayList<File> fileArrayList = new ArrayList<>();
        for (File file : files) {
            fileArrayList.add(file);
        }

        return fileArrayList;
    }

    public ArrayList<File> getImagesFiles() {
        ArrayList<File> fileArrayList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            if (fileType(files[i].getName().toLowerCase()).contains(FileFormat.image)) {
                fileArrayList.add(files[i]);
            }
        }
        return fileArrayList;
    }

    public ArrayList<File> getOtherFiles() {
        ArrayList<File> fileArrayList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (fileType(files[i].getName().toLowerCase()).contains(FileFormat.other)) {
                Log.d(TAG, "Other FileName:" + files[i].getName());
                fileArrayList.add(files[i]);
            }
        }
        return fileArrayList;
    }

    public int getAudioFilesCount() {
        int count = 0;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                Log.d(TAG, "FileName:" + files[i].getName());
                if (fileType(files[i].getName().toLowerCase()).contains(FileFormat.audio)) {
                    count++;
                }
            }
        }
        return count;
    }


    public int getVideoFilesCount() {
        int count = 0;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                if (fileType(files[i].getName().toLowerCase()).contains(FileFormat.video)) {
                    count++;
                }
            }
        }
        return count;
    }


    public int getImagesFilesCount() {
        int count = 0;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                if (fileType(files[i].getName().toLowerCase()).contains(FileFormat.image)) {
                    count++;
                }
            }
        }
        return count;
    }


    public int getOtherFilesCount() {
        int count = 0;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (fileType(files[i].getName().toLowerCase()).contains(FileFormat.other)) {
                    Log.d(TAG, "Others FileName:" + files[i].getName());
                    count++;
                }
            }
        }
        return count;
    }

}
