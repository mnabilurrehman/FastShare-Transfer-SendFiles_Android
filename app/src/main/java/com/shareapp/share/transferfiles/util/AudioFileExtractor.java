package com.shareapp.share.transferfiles.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.shareapp.share.transferfiles.model.MusicFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ji Janab on 5/3/2018.
 */

public class AudioFileExtractor {


    public List<MusicFile> getAllAudioFromDevice(final Context context) {

        final List<MusicFile> tempAudioList = new ArrayList<>();

//        Uri uri1 = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST, MediaStore.Audio.AudioColumns.DURATION};
//        Cursor c = context.getContentResolver().query(uri, projection, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%yourFolderName%"}, null);

        Cursor c = context.getContentResolver().query(uri,
                projection,
                null,
                null,
                null);

        if (c != null) {
            while (c.moveToNext()) {

                MusicFile audioModel = new MusicFile();
                String path = c.getString(0);
                String album = c.getString(1);
                String artist = c.getString(2);
                Long duration = c.getLong(3);


                String name = path.substring(path.lastIndexOf("/") + 1);

                audioModel.setFileName(name);
                audioModel.setAlbum(album);
                audioModel.setArtist(artist);
                audioModel.setFilePath(path);
                audioModel.setDuration(duration);

//                Log.e("Name :" + name, " Album :" + album);
//                Log.e("Path :" + path, " Artist :" + artist);

                tempAudioList.add(audioModel);
            }
            c.close();
        }

        return tempAudioList;
    }



    //converting long duration to  hhmmss


    public static String convertMillieToHMmSsNew(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;


        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }
    }
}
