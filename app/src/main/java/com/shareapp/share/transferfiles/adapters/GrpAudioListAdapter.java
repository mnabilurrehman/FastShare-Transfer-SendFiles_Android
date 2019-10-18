package com.shareapp.share.transferfiles.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.activity.GroupFilesTabActivity;
import com.shareapp.share.transferfiles.model.MusicFile;
import com.shareapp.share.transferfiles.widget.FilesCart;

import java.io.File;
import java.util.List;

public class GrpAudioListAdapter extends RecyclerView.Adapter<GrpAudioListAdapter.CustomViewHolder> {


    private static final String TAG = "AppLog/GrpAudioListAdapter";

    Context context1;
    List<MusicFile> stringList;
    GrpAudioListAdapter.CustomViewHolder customViewHolder;
    FilesCart filesCart;




    public GrpAudioListAdapter(Context context, List<MusicFile> musicFiles) {
        context1 = context;
        stringList = musicFiles;
        filesCart = new FilesCart(context);

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context1).inflate(R.layout.list_item_audio, parent, false);

        customViewHolder = new GrpAudioListAdapter.CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final String path = stringList.get(position).getFilePath();

        if (filesCart.isPresent(path)) {
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheck.setVisibility(View.INVISIBLE);
        }

        holder.tvFileName.setText(stringList.get(position).getFileName());
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GroupFilesTabActivity) context1).setReturnIntentResult(path);
            }
        });


    }

    @Override
    public int getItemCount() {
        return stringList.size();
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
