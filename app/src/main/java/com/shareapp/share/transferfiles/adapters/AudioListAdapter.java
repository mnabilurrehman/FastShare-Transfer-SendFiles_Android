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
import com.shareapp.share.transferfiles.model.MusicFile;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.shareapp.share.transferfiles.activity.MainSwipeActivity;

import java.io.File;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by Ji Janab on 5/3/2018.
 */

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.CustomViewHolder> {

    private static final String TAG = "AppLog/AudioListAdapter";

    Context context1;
    List<MusicFile> stringList;
    AudioListAdapter.CustomViewHolder customViewHolder;
    FilesCart filesCart;
    Button bSend;

    public AudioListAdapter(Context context, List<MusicFile> musicFiles) {
        context1 = context;
        stringList = musicFiles;
        filesCart = new FilesCart(context);
        try {
            bSend = ((MainSwipeActivity) context).findViewById(R.id.bSend);
        } catch (Exception e) {
            bSend = ((MainActivity) context).findViewById(R.id.bSend);
        }
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

    @Override
    public AudioListAdapter.CustomViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context1).inflate(R.layout.list_item_audio, parent, false);

        customViewHolder = new AudioListAdapter.CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        String path = stringList.get(position).getFilePath();

        holder.tvFileName.setText(stringList.get(position).getFileName());

        if (filesCart.isPresent(path)) {
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheck.setVisibility(View.INVISIBLE);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = stringList.get(position).getFilePath();
                Log.d(TAG, "Audio File Selected: " + path);

                int quantity = filesCart.getQuantity();
                if (quantity == 0) {
                    bSend.setText("Send (0)");
//                    bSend.setActivated(false);
                } else {
                    bSend.setText("Send (" + filesCart.getQuantity() + ")");
//                    bSend.setActivated(true);
                }


                Boolean isAdded = filesCart.addFile(path);
                Log.i(TAG, "File path added: " + isAdded);
                if (isAdded) {
                    bSend.setText("Send (" + filesCart.getQuantity() + ")");
                    Toasty.success(context1, "File Added").show();
                    holder.ivCheck.setVisibility(View.VISIBLE);
                    quantity = filesCart.getQuantity();
                    if (quantity == 0) {
                        bSend.setText("Send (0)");
                        bSend.setActivated(false);
                    } else {
                        bSend.setText("Send (" + filesCart.getQuantity() + ")");
                        bSend.setActivated(true);
                    }
                } else {
                    if (filesCart.removeFile(path)) {
                        bSend.setText("Send (" + filesCart.getQuantity() + ")");
                        Toasty.error(context1, "File Removed").show();
                        holder.ivCheck.setVisibility(View.INVISIBLE);
                        quantity = filesCart.getQuantity();
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

        MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
        File audioFile = new File(path);
        Uri uri = (Uri) Uri.fromFile(audioFile);
        byte[] rawArt;
        Bitmap art;
        BitmapFactory.Options bfo = new BitmapFactory.Options();

        mediaMetadataRetriever.setDataSource(context1, uri);
        rawArt = mediaMetadataRetriever.getEmbeddedPicture();

        if (rawArt != null) {
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
            holder.ivThumbnail.setImageBitmap(art);
        }

        holder.tvFileSize.setText((audioFile.length() / 1024 / 1024) + " MB");

    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
}