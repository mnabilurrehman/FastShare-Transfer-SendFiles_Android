package com.shareapp.share.transferfiles.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.model.Model_images;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;

public class SliderImagesRecyclerAdapter extends RecyclerView.Adapter<SliderImagesRecyclerAdapter.CustomViewHolder> {

    private final static String TAG = "AppLog/SliderImagesRecy";

    private Context context;
//    private ArrayList<Model_images> imagesArrayList;
    private ArrayList<String> imagesArrayList;
    private CustomViewHolder customViewHolder;

    public SliderImagesRecyclerAdapter(Context context, ArrayList<String> imagesArrayList) {
        this.context = context;
        this.imagesArrayList = imagesArrayList;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slider_images, parent, false);

        customViewHolder = new CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

//        for (Model_images x : imagesArrayList) {
//            for (String path : imagesArrayList) {
//                File imgFile = new File(path);
//
//                if (imgFile.exists()) {
//                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    try {
//                        holder.ivThumbnail.setImageBitmap(myBitmap);
//                    } catch (Exception e) {
//                        Log.e(TAG, "Exception: " + e.getMessage());
//                    }
//                } else {
//                    Log.i(TAG, "File Doesn't exist" + path);
//                }

//                Log.i(TAG, "ListPicsLinks" + path);
//                Picasso.get().load("file:"+path).config(Bitmap.Config.RGB_565).fit().centerCrop().into(holder.ivThumbnail);
//                Glide.with(context).load(x.getAl_imagepath())
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(false)
//                        .into(holder.ivThumbnail);
//            }
//        }
//
//
//        if (imagesArrayList.get(0) != null){
//            Glide.with(context).load(imagesArrayList.get(0))
//                    .into(holder.ivThumbnail);
//        }
//        if (imagesArrayList.get(1) != null){
//            Glide.with(context).load(imagesArrayList.get(1))
//                    .into(holder.ivThumbnail);
//        }
//        if (imagesArrayList.get(2) != null){
//            Glide.with(context).load(imagesArrayList.get(2))
//                    .into(holder.ivThumbnail);
//        }
//        if (imagesArrayList.get(3) != null){
//            Glide.with(context).load(imagesArrayList.get(3))
//                    .into(holder.ivThumbnail);
//        }

//        for ( Model_images x : imagesArrayList) {
            Log.i(TAG, "ListPicsLinks" + imagesArrayList.get(position));
            Glide.with(context).load(imagesArrayList.get(position))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(false)
                    .into(holder.ivThumbnail);
//        }
//        Glide.with(context).load(imagesArrayList.get(position).getAl_imagepath().get(0))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
//        int size = 0;
//        for (Model_images x : imagesArrayList) {
//            size = x.getAl_imagepath().size() + size;
//        }
////        size = imagesArrayList.size() + size;
//        Log.d(TAG, "Slider Image Size: " + size);
//        return size;
        return imagesArrayList.size();
    }

    protected static class CustomViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView ivThumbnail;

        public CustomViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            ivThumbnail = (ImageView) view.findViewById(R.id.iv_image);
        }
    }
}
