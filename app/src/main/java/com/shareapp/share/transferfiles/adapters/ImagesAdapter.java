package com.shareapp.share.transferfiles.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.activity.PhotosActivity;
import com.shareapp.share.transferfiles.model.Model_images;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

/**
 * Created by Nabil ur Rehman on 5/4/2018.
 */

public class ImagesAdapter extends ArrayAdapter<Model_images> {

    private static final String TAG = "AppLog/ImagesAdapter";

    Context context;
    ViewHolder viewHolder;
    ArrayList<Model_images> al_menu = new ArrayList<>();
    int int_position;
    private FilesCart filesCart;
    private Button bSend;

    public ImagesAdapter(Context context, ArrayList<Model_images> al_menu, int int_position) {
        super(context, R.layout.adapter_photosfolder, al_menu);
        this.al_menu = al_menu;
        this.context = context;
        this.int_position = int_position;
        filesCart = new FilesCart(context);
        bSend = ((PhotosActivity) context).findViewById(R.id.bSend);
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

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_photosfolder, parent, false);
            viewHolder.tv_foldern = (TextView) convertView.findViewById(R.id.tv_folder);
            viewHolder.tv_foldersize = (TextView) convertView.findViewById(R.id.tv_folder2);
            viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            viewHolder.ivCheck = (ImageView) convertView.findViewById(R.id.ivCheck1);
            viewHolder.ivSelected = (ImageView) convertView.findViewById(R.id.ivSelected);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.ll);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivSelected.setVisibility(View.INVISIBLE);

        final View view = convertView;
        viewHolder.ivCheck.setImageResource(R.drawable.ic_videoslected);

        final String path = al_menu.get(int_position).getAl_imagepath().get(position);

        int quantity = filesCart.getQuantity();
        if (quantity == 0) {
            bSend.setText("Send (0)");
        } else {
            bSend.setText("Send (" + filesCart.getQuantity() + ")");
        }

        final ImageView viewHoldersa = viewHolder.ivSelected;

        if (filesCart.isPresent(path)) {
            viewHolder.ivSelected.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivSelected.setVisibility(View.INVISIBLE);
        }

        String filename = path.substring(path.lastIndexOf("/") + 1);


        File file = new File(path);
        Float fileSize = Float.parseFloat(String.valueOf(file.length() / 1024 / 1024));

        viewHolder.tv_foldern.setText(filename);
        viewHolder.tv_foldersize.setText(fileSize + " MB");


        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filesCart.addFile(path)) {


                    viewHoldersa.setVisibility(View.VISIBLE);

                    Log.d(TAG, "addFile");
                    Toasty.success(context, "File Added").show();
                    int quantity = filesCart.getQuantity();
                    if (quantity == 0) {
                        bSend.setText("Send (0)");
                        bSend.setActivated(false);
                    } else {
                        bSend.setText("Send (" + filesCart.getQuantity() + ")");
                        bSend.setActivated(true);
                    }
                } else {
                    if (filesCart.removeFile(path)) {
                        viewHoldersa.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "removeFile");
                        int quantity = filesCart.getQuantity();
                        Toasty.error(context, "File Removed").show();
                        if (quantity == 0) {
                            bSend.setText("Send (0)");
                            bSend.setActivated(false);
                        } else {
                            bSend.setText("Send (" + filesCart.getQuantity() + ")");
                            bSend.setActivated(true);
                        }
                    }
                }
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
