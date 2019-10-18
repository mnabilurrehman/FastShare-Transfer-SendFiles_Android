package com.shareapp.share.transferfiles.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.activity.MainActivity;
import com.shareapp.share.transferfiles.util.ApkInfoExtractor;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.shareapp.share.transferfiles.activity.MainSwipeActivity;

import java.io.File;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by Ji Janab on 4/26/2018.
 */

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.CustomViewHolder> {

    private final static String TAG = "AppLog/AppsAdapter";

    Context context1;
    List<String> stringList;
    List<ResolveInfo> resolveInfos;
    FilesCart filesCart;
    Button bSend;

    public AppsAdapter(Context context, List<ResolveInfo> list) {
        context1 = context;
//        stringList = null;
        resolveInfos = list;
        filesCart = new FilesCart(context);
        try {
            bSend = ((MainSwipeActivity) context).findViewById(R.id.bSend);
        } catch (Exception e) {
            bSend = ((MainActivity) context).findViewById(R.id.bSend);
        }
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

    @Override
    public AppsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view2 = LayoutInflater.from(context1).inflate(R.layout.list_item_apps, parent, false);
        AppsAdapter.CustomViewHolder viewHolder = new AppsAdapter.CustomViewHolder(view2);
        return viewHolder;
    }

    public boolean isSystemPackage(ResolveInfo resolveInfo) {
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public void onBindViewHolder(final AppsAdapter.CustomViewHolder viewHolder, final int position) {

        final ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(context1);

//        if (stringList == null){
//            Log.d(TAG, "stringList is null");
//        }
//
//        for (ResolveInfo resolveInfo : resolveInfos) {
//            ActivityInfo activityInfo = resolveInfo.activityInfo;
//            if (!isSystemPackage(resolveInfo)) {
//                stringList.add(activityInfo.applicationInfo.packageName);
//            }
//        }

        final String path = resolveInfos.get(position).activityInfo.applicationInfo.publicSourceDir;


        final String ApplicationPackageName = (String) resolveInfos.get(position).activityInfo.applicationInfo.packageName;
        String ApplicationLabelName = apkInfoExtractor.GetAppName(ApplicationPackageName);
        Drawable drawable = apkInfoExtractor.getAppIconByPackageName(ApplicationPackageName);

//        String path = apkInfoExtractor.GetAllInstalledApkInfo();

        viewHolder.tvAppName.setText(ApplicationLabelName);

        viewHolder.tvAppSize.setText((new File(path).length() / 1024 / 1024) + " MB");

        viewHolder.ivIcon.setImageDrawable(drawable);

        //Adding click listener on CardView to open clicked application directly from here .
//        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = context1.getPackageManager().getLaunchIntentForPackage(ApplicationPackageName);
//                if (intent != null) {
//                    context1.startActivity(intent);
//                } else {
//                    Toast.makeText(context1, ApplicationPackageName + " Error, Please Try Again.", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        if (filesCart.isPresent(path)) {
            viewHolder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivCheck.setVisibility(View.INVISIBLE);
        }

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filesCart.addFile(path)) {
                    Toasty.success(context1, "File Added").show();
                    viewHolder.ivCheck.setVisibility(View.VISIBLE);
                    int quantity = filesCart.getQuantity();
                    if (quantity == 0) {
                        bSend.setText("Send (0)");
//                        bSend.setActivated(false);
                    } else {
                        bSend.setText("Send (" + filesCart.getQuantity() + ")");
//                        bSend.setActivated(true);
                    }
                } else {
                    if (filesCart.removeFile(path)) {
                        Toasty.error(context1, "File Removed").show();
                        viewHolder.ivCheck.setVisibility(View.INVISIBLE);
                        int quantity = filesCart.getQuantity();
                        if (quantity == 0) {
                            bSend.setText("Send (0)");
//                            bSend.setActivated(false);
                        } else {
                            bSend.setText("Send (" + filesCart.getQuantity() + ")");
//                            bSend.setActivated(true);
                        }
                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return resolveInfos.size();
    }

}
