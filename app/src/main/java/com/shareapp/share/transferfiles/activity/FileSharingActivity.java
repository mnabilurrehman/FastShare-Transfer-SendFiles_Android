package com.shareapp.share.transferfiles.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.transfer.TransferService;
import com.shareapp.share.transferfiles.wifimanager.ClientScanResult;
import com.shareapp.share.transferfiles.wifimanager.WifiApiManager;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static android.provider.MediaStore.Images;

public class FileSharingActivity extends AppCompatActivity {

    private static final String TAG = "AppLog/FileSharngActvty";

    private static final int SELECT_PICTURE = 1;
    private static final int SELECT_FILE = 2;
    private String username;

    Uri newImageUri;

    private WifiApiManager wifiApManager;
    private String[] values;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    //-------------- Client

    TextView textPort;

    static final int SocketServerPORT = 8080;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_sharing);

        wifiApManager = new WifiApiManager(this);
        listView = (ListView) findViewById(R.id.listView2);

        username = (String) getIntent().getExtras().get("name");
//        Intent i = new Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);

        textPort = (TextView) findViewById(R.id.port);
        textPort.setText("port: " + SocketServerPORT);


        Intent intent = new Intent(this, GroupFilesTabActivity.class);
        startActivityForResult(intent, SELECT_FILE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectClient sendImage = new selectClient(selectedImageUri);
                sendImage.execute((Void) null);
            }
            if (requestCode == SELECT_FILE) {
                String path = data.getExtras().get("FILE_PATH").toString();
                Log.d(TAG, "File received: " + path);
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                selectClient sendImage = new selectClient(uri);
                sendImage.execute((Void) null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        TransferService.startStopService(this, false);
        super.onDestroy();
    }

    public void bFileExplorerClicked(View view) {
        Intent intent = new Intent(this, GroupFilesTabActivity.class);
        startActivityForResult(intent, SELECT_FILE);
    }

    private class selectClient extends AsyncTask<Void, Void, Boolean> {

        Uri imageUri;

        selectClient(Uri selectedImageUri) {
            imageUri = selectedImageUri;
            newImageUri = selectedImageUri;

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            final ArrayList<ClientScanResult> clients = wifiApManager.getClientList(false);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    ClientScanResult clientScanResult;
                    values = new String[clients.size()];
                    if (clients.size() == 0) {

                        return;
                    }


                    //send to all in list of ip address

                    for (int i = 0; i < clients.size(); i++) {
                        clientScanResult = clients.get(i);
                        values[i] = "IpAddress: " + clientScanResult.getIpAddress();
                    }
                    sendMessage sendImage = new sendMessage(imageUri, clients);
                    sendImage.execute((Void) null);
//                    adapter = new ArrayAdapter<>(FileSharingActivity.this, R.layout.list_white_text, R.id.list_content, values);
//                    listView.setAdapter(adapter);
//                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            Toast.makeText(getApplicationContext(), clients.get(position).getIpAddress(), Toast.LENGTH_SHORT).show();
//
//                            sendMessage sendImage = new sendMessage(imageUri, clients);
//                            sendImage.execute((Void) null);
//
//
//
//                        }
//                    });
                }
            });
            return true;
        }
    }

    private class sendMessage extends AsyncTask<Void, Void, Boolean> {

        Uri selectedImage;
        ArrayList<ClientScanResult> ipAddress;

        sendMessage(Uri imageUri, ArrayList<ClientScanResult> ipAddress) {

            selectedImage = imageUri;
            this.ipAddress = ipAddress;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            ClientSenderThread clientSenderThread = new ClientSenderThread(ipAddress, SocketServerPORT);
            clientSenderThread.start();

//            Intent intent = new Intent(FileSharingActivity.this, MessageActivity.class);
//            intent.putExtra("name", username);
//            startActivity(intent);


            return true;
        }
    }

    private String getPath(Uri uri) {
        String[] projection = new String[]{Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(Images.Media.DATA);
        cursor.moveToFirst();
        String returnPath = cursor.getString(column_index);
        cursor.close();
        return returnPath;
    }

    private class ClientSenderThread extends Thread {
        ArrayList<ClientScanResult> dstAddress;
        int dstPort;

        ClientSenderThread(ArrayList<ClientScanResult> address, int port) {
            dstAddress = address;
            dstPort = port;
        }

        @Override
        public void run() {
            Socket socket;
            for (int i = 0; i < dstAddress.size(); i++) {


                try {
                    socket = new Socket(dstAddress.get(i).getIpAddress(), dstPort);

                    FileTransferThread fileTransferThread = new FileTransferThread(socket);
                    fileTransferThread.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class FileTransferThread extends Thread {
        Socket socket;

        FileTransferThread(Socket socket) {
            this.socket = socket;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
//            File file = new File(getPath(newImageUri));
            File file = new File(newImageUri.getPath().toString());

            String fileName = file.getName();


            byte[] bytes = new byte[(int) file.length()];
            BufferedInputStream bis;

            try {
                bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytes, 0, bytes.length);

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeUTF(fileName);
                oos.writeObject(bytes);
                oos.flush();
                socket.close();

                final String sentMsg = "File sent to: " + socket.getInetAddress();

                FileSharingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FileSharingActivity.this, sentMsg, Toast.LENGTH_LONG).show();
                        FileSharingActivity.this.onBackPressed();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}