package com.shareapp.share.transferfiles.activity;

import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.transfer.TransferService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HotspotReceiveActivity extends AppCompatActivity {

    private static final String TAG = "AppLog/HotsptReceiveAct";

    public int a;
    public int b;
    public String password;
    public String APname;

    private static int g;
    private static int h;
    private static int i;
    private static int j;
    private WifiManager wifiManager;
    private String logTAG;
    private int wifiState;
    private boolean o;
//    ApManager apManager;

    private Button bGetConnectivityCode;

    private HotspotReceiveActivity wifiAPController;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot_receive);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        apManager = new ApManager(this);

        bGetConnectivityCode = (Button) findViewById(R.id.bReceive);

        wifiAPController = new HotspotReceiveActivity();
        wifiAPController.wifiToggle("mHotspot", "12345678", wifiManager, this);

//        apManager.createNewNetwork(getString(R.string.app_name) + " Hotspot", "123456789");
    }

    @Override
    protected void onDestroy() {
        TransferService.startStopService(this, false);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

//        wifiAPController.wifiToggle(false);
        super.onBackPressed();

    }


    class wifiControllerTask extends AsyncTask {
        HotspotReceiveActivity wifiAPControllerClass;
        boolean a;
        boolean b;
        Context mContext;

        public wifiControllerTask(HotspotReceiveActivity wifiAPController, boolean arg3, boolean arg4, Context context) {
            this.wifiAPControllerClass = wifiAPController;
            this.a = arg3;
            this.b = arg4;
            this.mContext = context;
        }

        protected Void a(Void[] arg3) {
            try {
                HotspotReceiveActivity.wifiToggle(this.wifiAPControllerClass, this.a);
            } catch (Exception v0) {
            }
            return null;
        }

        public void a() {
            int sdkCurrentVersion = 21;
            try {
                if (this.a) {
                    if (Build.VERSION.SDK_INT < sdkCurrentVersion) {
                        return;
                    }

                    this.wifiAPControllerClass.wifiToggle(this.mContext);
                    return;
                }

                if (Build.VERSION.SDK_INT < sdkCurrentVersion) {
                    return;
                }
            } catch (Exception v0) {
                Log.e("noti error", v0.getMessage());
            }
        }

        protected void a(Void arg2) {
            super.onPostExecute(arg2);
            try {
                this.a();
            } catch (IllegalArgumentException v0) {
                try {
                    this.a();
                } catch (Exception v0_1) {
                }
            }

            if (this.b) {
                this.wifiAPControllerClass.finish();
            }
        }

        protected Object doInBackground(Object[] arg2) {
            return this.a(((Void[]) arg2));
        }

        protected void onPostExecute(Object arg1) {
            this.a(((Void) arg1));


        }

        protected void onPreExecute() {
            super.onPreExecute();
        }
    }


    static {
        HotspotReceiveActivity.g = 0;
        HotspotReceiveActivity.h = 0;
        HotspotReceiveActivity.i = 1;
        HotspotReceiveActivity.j = 4;
    }

    public HotspotReceiveActivity() {
        super();
        this.a = 2;
        this.b = 3;
        this.logTAG = "AppLog/HotspotRcvActvty";
        this.wifiState = -1;
        this.o = false;
    }

    static int wifiToggle(HotspotReceiveActivity wifiAPController, boolean wifiToggleFlag) {
        return wifiAPController.wifiToggle(wifiToggleFlag);
    }

    private void initWifiAPConfig(WifiConfiguration wifiConfiguration) {
        wifiConfiguration.SSID = "ShareData Hotspot";
//        wifiConfiguration.preSharedKey = "SomeKey1";
        wifiConfiguration.hiddenSSID = false;
        wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

    }

    private int wifiToggle(boolean wifiToggleFlag) {
        int wifiState;
        String stateString;
        StringBuilder message;
        long sleepTimeout = 500;
        int maxAttemptCount = 10;
        int errorState = -1;
        Log.d(this.logTAG, "*** setWifiApEnabled CALLED **** " + wifiToggleFlag);
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        initWifiAPConfig(wifiConfiguration);
        if ((wifiToggleFlag) && this.wifiState == errorState) {
            this.wifiState = this.wifiManager.getWifiState();
        }

        if (!(!wifiToggleFlag || this.wifiManager.getConnectionInfo() == null)) {
            Log.d(this.logTAG, "disable wifi: calling");
            this.wifiManager.setWifiEnabled(false);
            int attemptCount = maxAttemptCount;
            while (attemptCount > 0) {
                if (this.wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {//1
                    break;
                }

                Log.d(this.logTAG, "disable wifi: waiting, pass: " + (10 - attemptCount));
                try {
                    Thread.sleep(sleepTimeout);
                    --attemptCount;
                } catch (Exception v4_1) {
                }
            }

            Log.d(this.logTAG, "disable wifi: done, pass: " + (10 - attemptCount));
        }

        try {
            message = new StringBuilder();
            stateString = wifiToggleFlag ? "enabling" : "disabling";
            Log.d(this.logTAG, message.append(stateString).append(" wifi ap: calling").toString());
            Log.d(this.logTAG, this.APname);
            Log.d(this.logTAG, this.password);
            Log.d(this.logTAG, "" + this.wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class).invoke(this.wifiManager, wifiConfiguration, true).toString());
            int res = this.wifiManager.addNetwork(wifiConfiguration);
            Log.d(this.logTAG, "" + res);
            wifiState = (int) this.wifiManager.getClass().getMethod("getWifiApState").invoke(this.wifiManager);
            Log.d(this.logTAG, "" + wifiState);
        } catch (Exception v0_1) {
            Log.e("wifi", v0_1.getMessage());
            wifiState = errorState;
        }

        while (maxAttemptCount > 0) {
            if (this.wifiToggle() != HotspotReceiveActivity.h && this.wifiToggle() != this.b && this.wifiToggle() != HotspotReceiveActivity.j) {
                break;
            }
            message = new StringBuilder();
            stateString = wifiToggleFlag ? "enabling" : "disabling";
            Log.d(this.logTAG, message.append(stateString).append(" wifi ap: waiting, pass: ").append(10 - maxAttemptCount).toString());
            sleepTimeout = 500;
            try {
                Thread.sleep(sleepTimeout);
                --maxAttemptCount;
            } catch (Exception v0_1) {
            }
        }
        message = new StringBuilder();
        stateString = wifiToggleFlag ? "enabling" : "disabling";
        Log.d(this.logTAG, message.append(stateString).append(" wifi ap: done, pass: ").append(10 - maxAttemptCount).toString());

        if (!wifiToggleFlag) {
            if ((this.wifiState >= WifiManager.WIFI_STATE_ENABLING && this.wifiState <= WifiManager.WIFI_STATE_UNKNOWN) || (this.o)) {
                Log.d(this.logTAG, "enable wifi: calling");
                this.wifiManager.setWifiEnabled(true);
            }

            this.wifiState = errorState;
            return wifiState;
        }
        return wifiState;
    }

    public int wifiToggle() {
        int result;
        int v4 = 10;
        try {
            result = (int) this.wifiManager.getClass().getMethod("getWifiApState").invoke(this.wifiManager);
        } catch (Exception v0) {
            result = -1;
        }

        if (result >= v4) {
            HotspotReceiveActivity.g = v4;
        }

        HotspotReceiveActivity.h = HotspotReceiveActivity.g;
        HotspotReceiveActivity.i = HotspotReceiveActivity.g + 1;
        this.a = HotspotReceiveActivity.g + 2;
        this.b = HotspotReceiveActivity.g + 3;
        HotspotReceiveActivity.j = HotspotReceiveActivity.g + 4;
        return result;
    }

    public void wifiToggle(Context context) {
        Intent v0 = new Intent(context, HotspotReceiveActivity.class);
    }

    public void wifiToggle(String apname, String pass, WifiManager wifiManager, Context context) {
        boolean v2 = true;
        if (this.wifiManager == null) {
            this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }

        this.APname = apname;
        this.password = pass;
        int v0 = this.wifiToggle() == this.b || this.wifiToggle() == this.a ? 1 : 0;
        if (v0 != 0) {
            v2 = false;
        }

        new wifiControllerTask(this, v2, false, context).execute(new Void[0]);
    }

    public static String getCurrentSsid(Context context) {
        String ssid = "kuch Nhi";
        android.net.ConnectivityManager connManager = (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo networkInfo = connManager.getNetworkInfo(android.net.ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final android.net.wifi.WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !android.text.TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }


    public void clicked(View view) {
        TextView textView = (TextView) view;
//        Settings settings = new Settings(this);
//        ((TextView) findViewById(R.id.tvDevice)).setText(settings.getString(Settings.Key.DEVICE_NAME));
    }

    public static String getWifiSSID(Context context) {
        if (context == null) {
            return "null";
        }
        final Intent intent = context.registerReceiver(
                null, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        if (intent != null) {
            final WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            if (wifiInfo != null) {
                final String ssid = wifiInfo.getSSID();
                if (ssid != null) {
                    return ssid;
                }
            }
        }
        return "nothing";
    }


}
