package com.shareapp.share.transferfiles.adapters;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.activity.GroupFilesTabActivity;
import com.shareapp.share.transferfiles.util.ApkInfoExtractor;
import com.shareapp.share.transferfiles.widget.FilesCart;

import java.io.File;
import java.util.List;

public class GrpAppsAdapter extends RecyclerView.Adapter<GrpAppsAdapter.CustomViewHolder> {


    private final static String TAG = "AppLog/GrpAppsAdap";

    Context context1;
    List<String> stringList;
    List<ResolveInfo> resolveInfos;
    FilesCart filesCart;


    public GrpAppsAdapter(Context context, List<ResolveInfo> list) {
        context1 = context;
//        stringList = null;
        resolveInfos = list;
        filesCart = new FilesCart(context);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(context1).inflate(R.layout.list_item_apps, parent, false);
        GrpAppsAdapter.CustomViewHolder viewHolder = new CustomViewHolder(view2);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(CustomViewHolder viewHolder, int position) {
        final ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(context1);
        final String path = resolveInfos.get(position).activityInfo.applicationInfo.publicSourceDir;

        final String ApplicationPackageName = (String) resolveInfos.get(position).activityInfo.applicationInfo.packageName;
        String ApplicationLabelName = apkInfoExtractor.GetAppName(ApplicationPackageName);
        Drawable drawable = apkInfoExtractor.getAppIconByPackageName(ApplicationPackageName);

        viewHolder.tvAppName.setText(ApplicationLabelName);

        viewHolder.tvAppSize.setText((new File(path).length() / 1024 / 1024) + " MB");

        viewHolder.ivIcon.setImageDrawable(drawable);

        if (filesCart.isPresent(path)) {
            viewHolder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivCheck.setVisibility(View.INVISIBLE);
        }

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GroupFilesTabActivity) context1).setReturnIntentResult(path);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resolveInfos.size();
    }

    protected static class CustomViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView ivIcon;
        public TextView tvAppName;
        public TextView tvAppSize;
        private ImageView ivCheck;

        public CustomViewHolder(View view) {

            super(view);

            cardView = (CardView) view.findViewById(R.id.card_view);
            ivIcon = (ImageView) view.findViewById(R.id.ivThumbnail);
            tvAppName = (TextView) view.findViewById(R.id.tvFileName);
            tvAppSize = (TextView) view.findViewById(R.id.tvFileSize);
            ivCheck = (ImageView) view.findViewById(R.id.ivCheck);
        }
    }

}
