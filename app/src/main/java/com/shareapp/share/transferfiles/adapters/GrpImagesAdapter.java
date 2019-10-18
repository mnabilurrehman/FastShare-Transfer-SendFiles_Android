package com.shareapp.share.transferfiles.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.activity.GroupFilesTabActivity;
import com.shareapp.share.transferfiles.activity.GrpPhotosActivity;
import com.shareapp.share.transferfiles.config.ActivityContext;
import com.shareapp.share.transferfiles.model.Model_images;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

public class GrpImagesAdapter extends ArrayAdapter<Model_images> {


    private static final String TAG = "AppLog/GrpImgesAdptr";

    Context context;
    GrpImagesAdapter.ViewHolder viewHolder;
    ArrayList<Model_images> al_menu = new ArrayList<>();
    int int_position;
    private FilesCart filesCart;

    public GrpImagesAdapter(Context context, ArrayList<Model_images> al_menu, int int_position) {
        super(context, R.layout.adapter_photosfolder, al_menu);
        this.al_menu = al_menu;
        this.context = context;
        this.int_position = int_position;

        filesCart = new FilesCart(context);
    }


    @Override
    public int getCount() {

        Log.e("ADAPTER LIST SIZE", al_menu.get(int_position).getAl_imagepath().size() + "");
        return al_menu.get(int_position).getAl_imagepath().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (al_menu.get(int_position).getAl_imagepath().size() > 0) {
            return al_menu.get(int_position).getAl_imagepath().size();
        } else {
            return 1;
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            viewHolder = new GrpImagesAdapter.ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_photosfolder, parent, false);
            viewHolder.tv_foldern = (TextView) convertView.findViewById(R.id.tv_folder);
            viewHolder.tv_foldersize = (TextView) convertView.findViewById(R.id.tv_folder2);
            viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            viewHolder.ivCheck = (ImageView) convertView.findViewById(R.id.ivCheck1);
            viewHolder.ivSelected = (ImageView) convertView.findViewById(R.id.ivSelected);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.ll);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GrpImagesAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.ivSelected.setVisibility(View.INVISIBLE);

        final View view = convertView;
        viewHolder.ivCheck.setImageResource(R.drawable.ic_videoslected);

        final String path = al_menu.get(int_position).getAl_imagepath().get(position);

        int quantity = filesCart.getQuantity();

        final ImageView viewHoldersa = viewHolder.ivSelected;
//
//        if (filesCart.isPresent(path)) {
//            viewHolder.ivSelected.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.ivSelected.setVisibility(View.INVISIBLE);
//        }

        String filename = path.substring(path.lastIndexOf("/") + 1);


        File file = new File(path);
        Float fileSize = Float.parseFloat("" + (file.length() / 1024 / 1024));

        viewHolder.tv_foldern.setText(filename);
        viewHolder.tv_foldersize.setText(fileSize + " MB");


        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GroupFilesTabActivity) ActivityContext.GroupFilesTabActivity).setReturnIntentResult(path);
                ((GrpPhotosActivity) context).finish();
            }
        });

        Glide.with(context).load(al_menu.get(int_position).getAl_imagepath().get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(viewHolder.iv_image);


        return convertView;

    }


    private static class ViewHolder {
        TextView tv_foldern, tv_foldersize;
        ImageView iv_image;
        ImageView ivCheck;
        ImageView ivSelected;
        LinearLayout linearLayout;
    }

}
