package com.shareapp.share.transferfiles.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.adapters.ReceivedFilesAdapter;
import com.shareapp.share.transferfiles.widget.ReceivedFiles;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceivedAudioFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<File> arrayList;
    private ImageView ivEmptyList;

    public ReceivedAudioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_received_audio, container, false);

        arrayList = new ArrayList<>();
        ivEmptyList = (ImageView) view.findViewById(R.id.ivEmptyList);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // Passing the column number 1 to show online one column in each row.
        recyclerViewLayoutManager = new GridLayoutManager(getContext(), 1);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        arrayList = new ReceivedFiles().getAudioFiles();

        if (arrayList == null || arrayList.size() == 0) {
            ivEmptyList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            ivEmptyList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new ReceivedFilesAdapter(getActivity(), arrayList);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }
}
