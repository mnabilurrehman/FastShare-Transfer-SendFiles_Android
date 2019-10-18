package com.shareapp.share.transferfiles.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.activity.MainActivity;
import com.shareapp.share.transferfiles.model.Model_Video;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.shareapp.share.transferfiles.activity.MainSwipeActivity;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class LocalVideoAdapter extends RecyclerView.Adapter<LocalVideoAdapter.ViewHolder> {

    private final static String TAG = "AppLog/LocalVideoAdap";

    ArrayList<Model_Video> al_video;
    Context context;
    Activity activity;
    FilesCart filesCart;
    Button bSend;

    public LocalVideoAdapter(Context context, ArrayList<Model_Video> al_video, Activity activity) {
        this.al_video = al_video;
        this.context = context;
        this.activity = activity;
        try {
            bSend = ((MainSwipeActivity) context).findViewById(R.id.bSend);
        } catch (Exception e){
            bSend = ((MainActivity) context).findViewById(R.id.bSend);
        }
        filesCart = new FilesCart(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_image;
        LinearLayout rl_select;
        ImageView ivCheck;
        TextView tvFileName;
        TextView tvFileSize;

        public ViewHolder(View v) {
            super(v);
            ivCheck = (ImageView) v.findViewById(R.id.ivCheck);
            iv_image = (ImageView) v.findViewById(R.id.iv_image);
            rl_select = (LinearLayout) v.findViewById(R.id.rl_select);
            tvFileName = (TextView) v.findViewById(R.id.tvFileName);
            tvFileSize = (TextView) v.findViewById(R.id.tvFileSize);
        }
    }

    @NonNull
    @Override
    public LocalVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_videos, parent, false);

        ViewHolder viewHolder1 = new ViewHolder(view);

        return viewHolder1;
    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        holder.
//    }

    //    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

//file+ al_video.get(position).getStr_thumb()
        Glide.with(context).load(al_video.get(position).getStr_thumb())
                .skipMemoryCache(false)
                .into(holder.iv_image);
//        holder.rl_select.setBackgroundColor(Color.parseColor("#888FFF"));
//        holder.rl_select.setAlpha(0);

        final String path = al_video.get(position).getStr_path();

        File file = new File(path);
        Float fileSize = Float.parseFloat(String.valueOf(file.length() / 1024 / 1024));

        String filename = path.substring(path.lastIndexOf("/") + 1);
        holder.tvFileSize.setText(fileSize + " MB");
        holder.tvFileName.setText(filename);

        int quantity = filesCart.getQuantity();
        if (quantity == 0) {
            bSend.setText("Send (0)");
            bSend.setActivated(false);
        } else {
            bSend.setText("Send (" + filesCart.getQuantity() + ")");
            bSend.setActivated(true);
        }

        if (filesCart.isPresent(path)) {
            holder.ivCheck.setImageResource(R.drawable.ic_videoslected_s);
        } else {
            holder.ivCheck.setImageResource(R.drawable.ic_videoslected);
        }

        holder.rl_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filesCart.addFile(path)) {
                    Toasty.success(context, "File Added").show();
                    holder.ivCheck.setImageResource(R.drawable.ic_videoslected_s);
                    int quantity = filesCart.getQuantity();
                    if (quantity == 0) {
                        bSend.setText("Send (0)");
                        bSend.setActivated(false);
                    } else {
                        bSend.setText("Send (" + filesCart.getQuantity() + ")");
                        bSend.setActivated(true);
                    }
                } else {
                    Toasty.error(context, "File Removed").show();
                    holder.ivCheck.setImageResource(R.drawable.ic_videoslected);
                    if (filesCart.removeFile(path)) {
                        bSend.setText("Send (" + filesCart.getQuantity() + ")");
                    }
                    int quantity = filesCart.getQuantity();
                    if (quantity == 0) {
                        bSend.setText("Send (0)");
                        bSend.setActivated(false);
                    } else {
                        bSend.setText("Send (" + filesCart.getQuantity() + ")");
                        bSend.setActivated(true);
                    }
                }
                //To Play Video in new activity
//                Intent intent_gallery = new Intent(context, Activity_galleryview.class);
//                intent_gallery.putExtra("video", al_video.get(position).getStr_path());
//                activity.startActivity(intent_gallery);

            }
        });

    }

    @Override
    public int getItemCount() {
        return al_video.size();
    }
}
