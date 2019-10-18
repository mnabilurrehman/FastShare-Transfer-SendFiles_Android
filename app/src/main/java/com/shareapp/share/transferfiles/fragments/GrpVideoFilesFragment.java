package com.shareapp.share.transferfiles.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.adapters.GrpLocalVideoAdapter;
import com.shareapp.share.transferfiles.model.Model_Video;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;


public class GrpVideoFilesFragment extends Fragment {

    private static final String TAG = "AppLog/GrpVideoFilesFra";

    GrpLocalVideoAdapter obj_adapter;
    ArrayList al_video = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    private static final int REQUEST_PERMISSIONS = 100;
    private ImageView ivEmptyList;
    private AdView mAdView;

    public GrpVideoFilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grp_video_files, container, false);


        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view1);
        ivEmptyList = (ImageView) view.findViewById(R.id.ivEmptyList);
        recyclerViewLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        fn_checkpermission();

        return view;
    }

    private void fn_checkpermission() {
        /*RUN TIME PERMISSIONS*/

        if ((ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            Log.e("Else", "Else");
            fn_video();
        }
    }

    public void fn_video() {
        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name, column_id, thum;

        String absolutePathOfImage = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        if (cursor.getCount() > 0) {

            ivEmptyList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                Log.e("Column", absolutePathOfImage);
                Log.e("Folder", cursor.getString(column_index_folder_name));
                Log.e("column_id", cursor.getString(column_id));
                Log.e("thum", cursor.getString(thum));

                Model_Video obj_model = new Model_Video();
                obj_model.setBoolean_selected(false);
                obj_model.setStr_path(absolutePathOfImage);
                obj_model.setStr_thumb(cursor.getString(thum));

                al_video.add(obj_model);
            }
            obj_adapter = new GrpLocalVideoAdapter(getContext(), al_video, getActivity());
            Log.i(TAG, "obj_adapter Count: " + obj_adapter.getItemCount());
            recyclerView.setAdapter(obj_adapter);
        } else {
            ivEmptyList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

}
