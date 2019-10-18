package com.shareapp.share.transferfiles.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.util.Permissions;
import com.shareapp.share.transferfiles.discovery.Device;
import com.shareapp.share.transferfiles.util.Settings;
import com.shareapp.share.transferfiles.transfer.TransferService;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

/**
 * Display a list of devices available for receiving a transfer
 * <p>
 * mDNS (multicast DNS) is used to find other peers capable of receiving the transfer. Once a
 * device is selected, the transfer service is provided with the device information and the file.
 */
public class ShareActivity extends AppCompatActivity {

    private static final String TAG = "AppLog/ShareActivity";

    private ArrayList<Device> deviceArrayList = new ArrayList<>();

    private TextView mobileName1;
    private LinearLayout mobile1;
    private TextView mobileName2;
    private LinearLayout mobile2;
    private TextView mobileName3;
    private LinearLayout mobile3;
    private TextView mobileName4;
    private LinearLayout mobile4;
    private TextView mobileName5;
    private LinearLayout mobile5;

    private AdView mAdView;

    private PulsatorLayout pulsator;
    private final Map<String, Device> mDevices = new HashMap<>();

    /**
     * Adapter that discovers other devices on the network
     */
    private class DeviceAdapter extends ArrayAdapter<String> {

        /**
         * Maintain a mapping of device IDs to discovered devices
         */
//        private final Map<String, Device> mDevices = new HashMap<>();

        /**
         * Maintain a queue of devices to resolve
         */
        private final List<NsdServiceInfo> mQueue = new ArrayList<>();

        private NsdManager mNsdManager;
        private String mThisDeviceName;

        /**
         * Prepare to resolve the next service
         * <p>
         * For some inexplicable reason, Android chokes miserably when
         * resolving more than one service at a time. The queue performs each
         * resolution sequentially.
         */
        private void prepareNextService() {
            synchronized (mQueue) {
                mQueue.remove(0);
                if (mQueue.size() == 0) {
                    return;
                }
            }
            resolveNextService();
        }

        /**
         * Resolve the next service in the queue
         */
        private void resolveNextService() {
            NsdServiceInfo serviceInfo;
            synchronized (mQueue) {
                serviceInfo = mQueue.get(0);
            }
            Log.d(TAG, String.format("resolving \"%s\"", serviceInfo.getServiceName()));
            mNsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {
                @Override
                public void onServiceResolved(final NsdServiceInfo serviceInfo) {
                    Log.d(TAG, String.format("resolved \"%s\"", serviceInfo.getServiceName()));
                    final Device device = new Device(
                            serviceInfo.getServiceName(),
                            "",
                            serviceInfo.getHost(),
                            serviceInfo.getPort()
                    );
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDevices.put(serviceInfo.getServiceName(), device);
                            add(serviceInfo.getServiceName());
                        }
                    });
                    prepareNextService();
                }

                @Override
                public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                    Log.e(TAG, String.format("unable to resolve \"%s\": %d",
                            serviceInfo.getServiceName(), errorCode));
                    prepareNextService();
                }
            });
        }

        /**
         * Listener for discovery events
         */
        private NsdManager.DiscoveryListener mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                if (serviceInfo.getServiceName().equals(mThisDeviceName)) {
                    return;
                }
                Log.d(TAG, String.format("found \"%s\"; queued for resolving", serviceInfo.getServiceName()));
                boolean resolve;
                synchronized (mQueue) {
                    resolve = mQueue.size() == 0;
                    mQueue.add(serviceInfo);
                }
                if (resolve) {
                    resolveNextService();
                }
            }

            @Override
            public void onServiceLost(final NsdServiceInfo serviceInfo) {
                Log.d(TAG, String.format("lost \"%s\"", serviceInfo.getServiceName()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        remove(serviceInfo.getServiceName());
                        mDevices.remove(serviceInfo.getServiceName());
                    }
                });
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(TAG, "service discovery started");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG, "service discovery stopped");
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "unable to start service discovery");
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "unable to stop service discovery");
            }
        };

        DeviceAdapter() {
            super(ShareActivity.this, R.layout.view_simple_list_item, android.R.id.text1);
        }

        void start() {
            mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
            mNsdManager.discoverServices(Device.SERVICE_TYPE,
                    NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

            mThisDeviceName = new Settings(getContext()).getString(Settings.Key.DEVICE_NAME);
        }

        void stop() {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        }

        /**
         * Retrieve the specified device
         *
         * @param position device index
         * @return device at the specified position
         */
        Device getDevice(int position) {
            return mDevices.get(getItem(position));
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                convertView = layoutInflater.inflate(R.layout.view_simple_list_item, parent, false);
            }
            Device device = mDevices.get(getItem(position));

            Log.d(TAG, "Connected Devices: " + device.getName());

            if (!deviceArrayList.contains(device))
                deviceArrayList.add(device);

            if (deviceArrayList.get(0) != null) {
                mobileName1.setText(deviceArrayList.get(0).getName());
                mobile1.setVisibility(View.VISIBLE);
            } else {
                mobile1.setVisibility(View.INVISIBLE);
            }


            if (deviceArrayList.size() == 2)
                if (deviceArrayList.get(1) != null) {
                    mobileName2.setText(deviceArrayList.get(1).getName());
                    mobile2.setVisibility(View.VISIBLE);
                } else {
                    mobile2.setVisibility(View.INVISIBLE);
                }


            if (deviceArrayList.size() == 3)
                if (deviceArrayList.get(2) != null) {
                    mobileName3.setText(deviceArrayList.get(2).getName());
                    mobile3.setVisibility(View.VISIBLE);
                } else {
                    mobile3.setVisibility(View.INVISIBLE);
                }


            if (deviceArrayList.size() == 4)
                if (deviceArrayList.get(3) != null) {
                    mobileName4.setText(deviceArrayList.get(3).getName());
                    mobile4.setVisibility(View.VISIBLE);
                } else {
                    mobile4.setVisibility(View.INVISIBLE);
                }

            if (deviceArrayList.size() == 5)
                if (deviceArrayList.get(4) != null) {
                    mobileName5.setText(deviceArrayList.get(4).getName());
                    mobile5.setVisibility(View.VISIBLE);
                } else {
                    mobile5.setVisibility(View.INVISIBLE);
                }


            ((TextView) convertView.findViewById(android.R.id.text1)).setText(device.getName());
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(device.getHost().getHostAddress());
            ((ImageView) convertView.findViewById(android.R.id.icon)).setImageResource(R.drawable.ic_device);
            return convertView;
        }
    }

    private DeviceAdapter mDeviceAdapter;

    /**
     * Ensure that the intent passed to the activity is valid
     *
     * @return true if the intent is valid
     */
    private boolean isValidIntent() {
        return (getIntent().getAction().equals(Intent.ACTION_SEND_MULTIPLE) ||
                getIntent().getAction().equals(Intent.ACTION_SEND)) &&
                getIntent().hasExtra(Intent.EXTRA_STREAM);
    }

    /**
     * Given a SEND or SEND_MULTIPLE intent, build a list of URIs
     *
     * @return list of URIs
     */
    private ArrayList<Uri> buildUriList() {
        if (getIntent().getAction().equals(Intent.ACTION_SEND_MULTIPLE)) {
            return getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        } else {
            ArrayList<Uri> uriList = new ArrayList<>();
            uriList.add((Uri) getIntent().getParcelableExtra(Intent.EXTRA_STREAM));
            return uriList;
        }
    }

    /**
     * Finish initializing the activity
     */
    private void finishInit() {
        mDeviceAdapter = new DeviceAdapter();
        mDeviceAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
//                findViewById(R.id.progressBar).setVisibility(View.GONE);
//                pulsator.stop();
            }
        });
        mDeviceAdapter.start();

        final ArrayList<Uri> uriList = buildUriList();
        final ListView listView = findViewById(R.id.selectList);
        listView.setAdapter(mDeviceAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Device device = mDeviceAdapter.getDevice(position);
//
//                Intent startTransfer = new Intent(ShareActivity.this, TransferService.class);
//                startTransfer.setAction(TransferService.ACTION_START_TRANSFER);
//                startTransfer.putExtra(TransferService.EXTRA_DEVICE, device);
//                startTransfer.putParcelableArrayListExtra(TransferService.EXTRA_URIS, uriList);
//                startService(startTransfer);
//
//                FilesCart filesCart = new FilesCart(getApplicationContext());
//                filesCart.destroyCart();
//
//                Intent gotoMainActivity = new Intent(ShareActivity.this, MainSwipeActivity.class);
//                gotoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(gotoMainActivity);
//
//                // Close the activity
////                ShareActivity.this.setResult(RESULT_OK);
////                ShareActivity.this.finish();
//            }
//        });
        init(uriList);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(new Settings(this).getTheme());
        setContentView(R.layout.activity_share);

        mobileName1 = (TextView) findViewById(R.id.mobile1name);
        mobile1 = (LinearLayout) findViewById(R.id.mobile1);
        mobileName2 = (TextView) findViewById(R.id.mobile2name);
        mobile2 = (LinearLayout) findViewById(R.id.mobile2);
        mobileName3 = (TextView) findViewById(R.id.mobile3name);
        mobile3 = (LinearLayout) findViewById(R.id.mobile3);
        mobileName4 = (TextView) findViewById(R.id.mobile4name);
        mobile4 = (LinearLayout) findViewById(R.id.mobile4);
        mobileName5 = (TextView) findViewById(R.id.mobile5name);
        mobile5 = (LinearLayout) findViewById(R.id.mobile5);

        mobile1.setVisibility(View.INVISIBLE);
        mobile2.setVisibility(View.INVISIBLE);
        mobile3.setVisibility(View.INVISIBLE);
        mobile4.setVisibility(View.INVISIBLE);
        mobile5.setVisibility(View.INVISIBLE);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();

        // Ensure valid data is present in the intent
        if (isValidIntent()) {

            // Attempt to obtain permission if it is somehow missing
            if (Permissions.haveStoragePermission(this)) {
                finishInit();
            } else {
                Permissions.requestStoragePermission(this);
            }
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.activity_share_intent)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private void init(final ArrayList<Uri> x) {
        mobile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deviceArrayList == null || deviceArrayList.get(0) == null) {
                    return;
                }
                ArrayList<Uri> uriList = x;

                Device device = deviceArrayList.get(0);

                Intent startTransfer = new Intent(ShareActivity.this, TransferService.class);
                startTransfer.setAction(TransferService.ACTION_START_TRANSFER);
                startTransfer.putExtra(TransferService.EXTRA_DEVICE, device);
                startTransfer.putParcelableArrayListExtra(TransferService.EXTRA_URIS, uriList);
                startService(startTransfer);

                FilesCart filesCart = new FilesCart(getApplicationContext());
                filesCart.destroyCart();

                Intent gotoMainActivity = new Intent(ShareActivity.this, MainSwipeActivity.class);
                gotoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gotoMainActivity);
            }
        });

        mobile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it doesn't exist don't initialize
                if (deviceArrayList.get(1) == null) {
                    return;
                }

                ArrayList<Uri> uriList = x;

                Device device = deviceArrayList.get(1);

                Intent startTransfer = new Intent(ShareActivity.this, TransferService.class);
                startTransfer.setAction(TransferService.ACTION_START_TRANSFER);
                startTransfer.putExtra(TransferService.EXTRA_DEVICE, device);
                startTransfer.putParcelableArrayListExtra(TransferService.EXTRA_URIS, uriList);
                startService(startTransfer);

                FilesCart filesCart = new FilesCart(getApplicationContext());
                filesCart.destroyCart();

                Intent gotoMainActivity = new Intent(ShareActivity.this, MainSwipeActivity.class);
                gotoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gotoMainActivity);
            }
        });

        mobile3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it doesn't exist don't initialize
                if (deviceArrayList.get(2) == null) {
                    return;
                }

                ArrayList<Uri> uriList = x;

                Device device = deviceArrayList.get(2);

                Intent startTransfer = new Intent(ShareActivity.this, TransferService.class);
                startTransfer.setAction(TransferService.ACTION_START_TRANSFER);
                startTransfer.putExtra(TransferService.EXTRA_DEVICE, device);
                startTransfer.putParcelableArrayListExtra(TransferService.EXTRA_URIS, uriList);
                startService(startTransfer);

                FilesCart filesCart = new FilesCart(getApplicationContext());
                filesCart.destroyCart();

                Intent gotoMainActivity = new Intent(ShareActivity.this, MainSwipeActivity.class);
                gotoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gotoMainActivity);
            }
        });

        mobile4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it doesn't exist don't initialize
                if (deviceArrayList.get(3) == null) {
                    return;
                }

                ArrayList<Uri> uriList = x;

                Device device = deviceArrayList.get(3);

                Intent startTransfer = new Intent(ShareActivity.this, TransferService.class);
                startTransfer.setAction(TransferService.ACTION_START_TRANSFER);
                startTransfer.putExtra(TransferService.EXTRA_DEVICE, device);
                startTransfer.putParcelableArrayListExtra(TransferService.EXTRA_URIS, uriList);
                startService(startTransfer);

                FilesCart filesCart = new FilesCart(getApplicationContext());
                filesCart.destroyCart();

                Intent gotoMainActivity = new Intent(ShareActivity.this, MainSwipeActivity.class);
                gotoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gotoMainActivity);
            }
        });

        mobile5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it doesn't exist don't initialize
                if (deviceArrayList.get(4) == null) {
                    return;
                }

                ArrayList<Uri> uriList = x;

                Device device = deviceArrayList.get(4);

                Intent startTransfer = new Intent(ShareActivity.this, TransferService.class);
                startTransfer.setAction(TransferService.ACTION_START_TRANSFER);
                startTransfer.putExtra(TransferService.EXTRA_DEVICE, device);
                startTransfer.putParcelableArrayListExtra(TransferService.EXTRA_URIS, uriList);
                startService(startTransfer);

                FilesCart filesCart = new FilesCart(getApplicationContext());
                filesCart.destroyCart();

                Intent gotoMainActivity = new Intent(ShareActivity.this, MainSwipeActivity.class);
                gotoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gotoMainActivity);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (Permissions.obtainedStoragePermission(requestCode, grantResults)) {
            finishInit();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.activity_share_permissions)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        TransferService.startStopService(this, false);
        if (mDeviceAdapter != null) {
            mDeviceAdapter.stop();
        }
        super.onDestroy();
    }
}
