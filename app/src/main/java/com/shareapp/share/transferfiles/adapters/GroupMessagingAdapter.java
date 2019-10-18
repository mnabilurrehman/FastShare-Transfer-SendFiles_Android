package com.shareapp.share.transferfiles.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;

public class GroupMessagingAdapter extends ArrayAdapter<String> {

    private static final String TAG = "AppLog/GrpMesagingAda";

    String[] messagesList;
    Context context;
    int viewId;
    int resource;

    public GroupMessagingAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull String[] objects) {
        super(context, resource, textViewResourceId, objects);
        messagesList = objects;
        this.context = context;
        viewId = textViewResourceId;
        this.resource = resource;
        Log.d(TAG, "Msgs Length: " + objects.length);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(resource, parent, false);
        } else {
            row = convertView;
        }

        TextView textView = row.findViewById(viewId);
        Log.d(TAG, "Msg: " + messagesList[position]);
        textView.setText(messagesList[position]);

        String path = Environment.getExternalStorageDirectory().toString() + "/Download/ShareData";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        final File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            if (files[i].getName().toLowerCase().contains(messagesList[position].toLowerCase())) {
                final int o = i;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFile(files[o], messagesList[position]);
                    }
                });
            }
        }
        return row;
    }

    @Override
    public int getCount() {
        return messagesList.length;
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

                Uri uri = Uri.fromFile(file);

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
            intent.setDataAndType(Uri.fromFile(file), "image/*");
            context.startActivity(intent);

        } else if (download_file_name.endsWith(".txt")) {


            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "text/html");
            context.startActivity(intent);

        } else if (download_file_name.toLowerCase().endsWith(".mp3") || download_file_name.endsWith(".MP3")
                || download_file_name.toLowerCase().endsWith(".wav") || download_file_name.endsWith(".ogg")){
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "audio/*");
            context.startActivity(intent);
        } else if (download_file_name.toLowerCase().endsWith(".mp4") || download_file_name.toLowerCase().endsWith(".3gp")
                || download_file_name.toLowerCase().endsWith(".mkv")){
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "video/*");
            context.startActivity(intent);
        } else if (download_file_name.toLowerCase().endsWith(".apk")){
            Intent intent =new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }
}
