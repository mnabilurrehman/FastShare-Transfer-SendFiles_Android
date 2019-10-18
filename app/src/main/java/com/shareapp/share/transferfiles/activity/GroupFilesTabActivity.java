package com.shareapp.share.transferfiles.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.adapters.TabsMsgGroupAdapter;
import com.shareapp.share.transferfiles.config.ActivityContext;
import com.shareapp.share.transferfiles.config.ReqeustKey;
import com.shareapp.share.transferfiles.transfer.TransferService;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.shareapp.share.transferfiles.wifimanager.ClientScanResult;
import com.shareapp.share.transferfiles.wifimanager.WifiApiManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class GroupFilesTabActivity extends AppCompatActivity implements ActionBar.TabListener {


    private final static String TAG = "AppLog/GrpFilsTbAtvty";

    private ViewPager viewPager;
    private ActionBar actionBar;
    private TabsMsgGroupAdapter tabsPagerAdapter;

    private FilesCart filesCart;


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
        setContentView(R.layout.activity_group_files_tab);

        ActivityContext.GroupFilesTabActivity = this;


        wifiApManager = new WifiApiManager(this);
        username = (String) getIntent().getExtras().get("name");
//        Intent i = new Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);

        textPort = (TextView) findViewById(R.id.port);
//        textPort.setText("port: " + SocketServerPORT);

//
//        Intent intent = new Intent(this, GroupFilesTabActivity.class);
//        startActivityForResult(intent, SELECT_FILE);


        filesCart = new FilesCart(this);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
// Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        tabsPagerAdapter = new TabsMsgGroupAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(tabsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));


    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    protected void onDestroy() {
        TransferService.startStopService(this, false);
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReqeustKey.OPEN_FILE_EXPLORER) {
            if (resultCode == Activity.RESULT_OK) {
                String path = data.getStringExtra("path");
                setReturnIntentResult(path);
            } else {
                Log.e(TAG, "No File Selected");
                Toasty.info(this, "No File Selected").show();
            }
        } else {
            Toasty.info(this, "Request code not matched").show();
        }
    }


    public void setReturnIntentResult(String path) {
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("FILE_PATH", path);
//        setResult(Activity.RESULT_OK, returnIntent);

        Log.d(TAG, "File received: " + path);
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        selectClient sendImage = new selectClient(uri);
        sendImage.execute((Void) null);
//
//        Intent intent = new Intent();
//        intent.putExtra("GetPath",file.toString());
//        intent.putExtra("GetFileName",file.getName());
//        setResult(RESULT_OK, intent);

        finish();
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
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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

                GroupFilesTabActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GroupFilesTabActivity.this, sentMsg, Toast.LENGTH_LONG).show();
                        GroupFilesTabActivity.this.onBackPressed();
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
