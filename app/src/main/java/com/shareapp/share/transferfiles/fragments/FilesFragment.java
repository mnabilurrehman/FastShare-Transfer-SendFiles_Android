package com.shareapp.share.transferfiles.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.config.ReqeustKey;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.necistudio.libarary.FilePickerActivity;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilesFragment extends Fragment {

    private final static String TAG = "AppLog/FilesFragment";

    private Button bDocument;
    private Button bSend;
    private Button bBrowse;

    private StorageChooser chooser;

    private FilesCart filesCart;

    private AdView mAdView;

    public FilesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);


        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        filesCart = new FilesCart(getContext());

        bSend = getActivity().findViewById(R.id.bSend);
        bBrowse = (Button) view.findViewById(R.id.bBrowse);
        bDocument = (Button) view.findViewById(R.id.bDocument);

        bDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FilePickerActivity.class);
                getActivity().startActivityForResult(intent, ReqeustKey.OPEN_FILE_EXPLORER);
            }
        });

        // Initialize Builder
        chooser = new StorageChooser.Builder()
                .withActivity(getActivity())
                .withFragmentManager(getActivity().getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true).showHidden(false)
                .setType(StorageChooser.FILE_PICKER)
                .build();

        // get path that the user has chosen
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                Log.e("SELECTED_PATH", path);
                if (filesCart.addFile(path)) {
                    bSend.setText("Send (" + filesCart.getQuantity() + ")");
                    Toasty.success(getContext(), "File Added").show();
                }
            }
        });

        chooser.setOnMultipleSelectListener(new StorageChooser.OnMultipleSelectListener() {
            @Override
            public void onDone(ArrayList<String> arrayList) {
                for (String path : arrayList) {
                    if (filesCart.addFile(path)) {
                        bSend.setText("Send (" + filesCart.getQuantity() + ")");
                    }
                }
                Toasty.success(getContext(), "Files Added").show();
            }
        });

        bBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooser.show();
            }
        });

        return view;
    }
}
