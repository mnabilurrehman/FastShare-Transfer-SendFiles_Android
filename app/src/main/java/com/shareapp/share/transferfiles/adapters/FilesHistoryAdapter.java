package com.shareapp.share.transferfiles.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareapp.share.transferfiles.BuildConfig;
import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.activity.HistoryActivity;
import com.shareapp.share.transferfiles.widget.FilesCart;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class FilesHistoryAdapter extends RecyclerView.Adapter<FilesHistoryAdapter.CustomViewHolder> {

    private static final String TAG = "AppLog/FilesHistoryAdap";

    private FilesHistoryAdapter.CustomViewHolder customViewHolder;
    private ArrayList<File> files;
    private Context context;
    private FilesCart filesCart;
    private Button bSend;

    public FilesHistoryAdapter(Context context, ArrayList<File> files) {
        this.context = context;
        this.files = files;
        filesCart = new FilesCart(context);
        bSend = ((HistoryActivity) context).findViewById(R.id.bSend);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false);

        customViewHolder = new FilesHistoryAdapter.CustomViewHolder(view);

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
            Log.e(TAG, "Error: " + e.getMessage());
        }

        if (rawArt != null) {
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
            holder.ivThumbnail.setImageBitmap(art);
        }

//        if (filesCart.isPresent(file.getPath())) {
//            holder.ivCheck.setVisibility(View.VISIBLE);
//        } else {
//            holder.ivCheck.setVisibility(View.INVISIBLE);
//        }

//        int quantity = filesCart.getQuantity();
//        if (quantity == 0) {
//            bSend.setText("Send (0)");
//        } else {
//            bSend.setText("Send (" + filesCart.getQuantity() + ")");
//        }


        holder.tvFileSize.setText((file.length() / 1024 / 1024) + " MB");

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile(file, file.getName());
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


    private void openFile(File fil, String msg) {
        File file = fil;
        String download_file_name = msg;

        if ((download_file_name.endsWith(".pdf")) || (download_file_name.endsWith(".PDF"))) {

            if (file.exists() && file.length() != 0) {

                Intent intent = new Intent();

                intent.setClassName("com.adobe.reader", "com.adobe.reader.AdobeReader");

                intent.setAction(android.content.Intent.ACTION_VIEW);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                Uri uri = FileProvider.getUriForFile(context, "com.shareapp.share.transferfiles.provider", file);
//                Uri uri = Uri.fromFile(file);

                intent.setDataAndType(uri, "application/pdf");

                try {

                    context.startActivity(intent);

                } catch (Exception e) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("No Application Found");
                    builder.setMessage("Download application from Android Market?");
                    builder.setPositiveButton(
                            "Yes, Please",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    Intent marketIntent = new Intent(
                                            Intent.ACTION_VIEW);
                                    marketIntent.setData(Uri
                                            .parse("market://details?id=com.adobe.reader"));
                                    context.startActivity(marketIntent);
                                }
                            });
                    builder.setNegativeButton("No, Thanks",
                            null);
                    builder.create().show();
                }

            }
        } else if ((download_file_name.endsWith(".jpg")) || (download_file_name.endsWith(".bmp")) ||
                (download_file_name.endsWith(".BMP")) || (download_file_name.endsWith(".png")) || (download_file_name.endsWith(".PNG")) || (download_file_name.endsWith(".gif")) || (download_file_name.endsWith(".GIF")) ||
                (download_file_name.endsWith(".JPG"))) {

            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(context, "com.shareapp.share.transferfiles.provider", file), "image/*");
//            intent.setDataAndType(Uri.fromFile(file), "image/*");
            context.startActivity(intent);

        } else if (download_file_name.endsWith(".txt")) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(context, "com.shareapp.share.transferfiles.provider", file), "text/html");
//            intent.setDataAndType(Uri.fromFile(file), "text/html");
            context.startActivity(intent);
        } else if (download_file_name.toLowerCase().endsWith(".mp3") || download_file_name.endsWith(".MP3")
                || download_file_name.toLowerCase().endsWith(".wav") || download_file_name.endsWith(".ogg")) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.fromFile(file), "audio/*");
            intent.setDataAndType(FileProvider.getUriForFile(context, "com.shareapp.share.transferfiles.provider", file), "audio/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else if (download_file_name.toLowerCase().endsWith(".mp4") || download_file_name.toLowerCase().endsWith(".3gp")
                || download_file_name.toLowerCase().endsWith(".mkv")) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(context, "com.shareapp.share.transferfiles.provider", file), "video/*");
//            intent.setDataAndType(Uri.fromFile(file), "video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else if (download_file_name.toLowerCase().endsWith(".apk")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(apkUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
//                intent.setDataAndType(FileProvider.getUriForFile(context, "com.shareapp.share.transferfiles.provider", file), "application/vnd.android.package-archive");
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                context.startActivity(intent);
            }
        }
    }

}
