package com.shareapp.share.transferfiles.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.activity.GroupFilesTabActivity;
import com.shareapp.share.transferfiles.model.Model_Video;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class GrpLocalVideoAdapter extends RecyclerView.Adapter<GrpLocalVideoAdapter.ViewHolder> {


    ArrayList<Model_Video> al_video;
    Context context;
    Activity activity;
    FilesCart filesCart;

    public GrpLocalVideoAdapter(Context context, ArrayList<Model_Video> al_video, Activity activity) {
        this.al_video = al_video;
        this.context = context;
        this.activity = activity;
        filesCart = new FilesCart(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_videos, parent, false);

        ViewHolder viewHolder1 = new ViewHolder(view);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

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

        if (filesCart.isPresent(path)) {
            holder.ivCheck.setImageResource(R.drawable.ic_videoslected_s);
        } else {
            holder.ivCheck.setImageResource(R.drawable.ic_videoslected);
        }

        holder.rl_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((GroupFilesTabActivity) context).setReturnIntentResult(path);

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

}
