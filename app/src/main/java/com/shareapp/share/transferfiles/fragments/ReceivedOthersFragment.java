package com.shareapp.share.transferfiles.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class ReceivedOthersFragment extends Fragment {

    private static final String TAG = "AppLog/RceivedOthersFra";

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<File> arrayList;
    private ImageView ivEmptyList;

    public ReceivedOthersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_received_others, container, false);

        arrayList = new ArrayList<>();
        ivEmptyList = (ImageView) view.findViewById(R.id.ivEmptyList);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // Passing the column number 1 to show online one column in each row.
        recyclerViewLayoutManager = new GridLayoutManager(getContext(), 1);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        arrayList = new ReceivedFiles().getOtherFiles();

        Log.d(TAG, "Others list size:" + arrayList.size());

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
