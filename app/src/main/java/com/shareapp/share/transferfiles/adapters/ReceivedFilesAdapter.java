package com.shareapp.share.transferfiles.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.activity.MainActivity;
import com.shareapp.share.transferfiles.widget.FilesCart;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ReceivedFilesAdapter extends RecyclerView.Adapter<ReceivedFilesAdapter.CustomViewHolder> {

    private static final String TAG = "AppLog/ReceivedFilesAda";

    private CustomViewHolder customViewHolder;
    private ArrayList<File> files;
    private Context context;
    private FilesCart filesCart;
    private Button bSend;

    public ReceivedFilesAdapter(Context context, ArrayList<File> files) {
        this.context = context;
        this.files = files;
        filesCart = new FilesCart(context);
        bSend = ((MainActivity) context).findViewById(R.id.bSend);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_others, parent, false);

        customViewHolder = new CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final File file = files.get(position);

        holder.tvFileName.setText(file.getName());


        MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
        Uri uri = (Uri) Uri.fromFile(file);
        byte[] rawArt = null;
        Bitmap art;
        BitmapFactory.Options bfo = new BitmapFactory.Options();

        try {
            mediaMetadataRetriever.setDataSource(context, uri);
            rawArt = mediaMetadataRetriever.getEmbeddedPicture();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        if (rawArt != null) {
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
            holder.ivThumbnail.setImageBitmap(art);
        }

        if (filesCart.isPresent(file.getPath())) {
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheck.setVisibility(View.INVISIBLE);
        }

        holder.tvFileSize.setText((file.length() / 1024 / 1024) + " MB");

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filesCart.addFile(file.getPath())) {
                    bSend.setText("Send (" + filesCart.getQuantity() + ")");
                    holder.ivCheck.setVisibility(View.VISIBLE);
                    Toasty.success(context, "File Added").show();
                } else if (filesCart.removeFile(file.getPath())) {
                    bSend.setText("Send (" + filesCart.getQuantity() + ")");
                    holder.ivCheck.setVisibility(View.INVISIBLE);
                    Toasty.error(context, "File Removed").show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    protected static class CustomViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView tvFileName;
        public ImageView ivThumbnail;
        public TextView tvFileSize;
        public ImageView ivCheck;


        public CustomViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            tvFileName = (TextView) view.findViewById(R.id.tvFileName);
            ivThumbnail = (ImageView) view.findViewById(R.id.ivThumbnail);
            tvFileSize = (TextView) view.findViewById(R.id.tvFileSize);
            ivCheck = (ImageView) view.findViewById(R.id.ivCheck);
        }
    }

}
