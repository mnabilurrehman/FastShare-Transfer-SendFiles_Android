package com.shareapp.share.transferfiles.activity;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.config.ReqeustKey;
import com.shareapp.share.transferfiles.transfer.TransferService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class GroupShareActivity extends AppCompatActivity {

    private static final String TAG = "AppLog/GroupShareActi";

    private String networkSSID = "Group-Sharing";

    private AdView mAdView;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_share);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.sample_ad_interstitial));

//        AdView adView = new AdView(this);
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mInterstitialAd.isLoaded()) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    public void joinGroupClicked(View view) {
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.d(TAG, "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.d(TAG, "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d(TAG, "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                Log.d(TAG, "onAdClosed");
                joinGroup();
            }

        });
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d(TAG, "The interstitial wasn't loaded yet.");
            joinGroup();
        }
    }

    private void joinGroup() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            Toasty.warning(this, "Turing on Wifi. Please try again..").show();
        } else {
            Toasty.info(this, "Joining group...").show();
            JoinWifiNetwork joinOne = new JoinWifiNetwork();
            joinOne.execute((Void) null);
        }
    }

    public void createGroupCLicked(View view) {
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.d(TAG, "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.d(TAG, "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d(TAG, "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                Log.d(TAG, "onAdClosed");
                createGroup();
            }

        });
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d(TAG, "The interstitial wasn't loaded yet.");
            createGroup();
        }
    }

    private void createGroup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                CreateWifiAccessPoint createOne = new CreateWifiAccessPoint();
                createOne.execute((Void) null);
            } else {
                goToSettings();
            }
        }
    }

    private void goToSettings() {
        Toasty.info(this, "Please allow to change system settings \n APP INFO > ADVANCED", 200000).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ReqeustKey.OPEN_SETTINGS);
            }
        }
    }

    public void ChatClicked(View view) {
        startMessageActivity();
    }

    private void startMessageActivity() {
        String str = android.os.Build.MODEL;
        Intent i = new Intent(GroupShareActivity.this, MessageActivity.class);
        i.putExtra("name", str);
        startActivity(i);
    }

    private void showInterstitialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    @Override
    protected void onDestroy() {
        TransferService.startStopService(this, false);
        super.onDestroy();
    }

    private class CreateWifiAccessPoint extends AsyncTask<Void, Void, Boolean> {
        {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            WifiManager wifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
            Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
            boolean methodFound = false;
            for (Method method : wmMethods) {
                if (method.getName().equals("setWifiApEnabled")) {
                    methodFound = true;
                    WifiConfiguration netConfig = new WifiConfiguration();
                    netConfig.SSID = networkSSID;
                    netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    try {
                        final boolean apStatus = (Boolean) method.invoke(wifiManager, netConfig, true);
                        for (Method isWifiApEnabledMethod : wmMethods)
                            if (isWifiApEnabledMethod.getName().equals("isWifiApEnabled")) {
                                while (!(Boolean) isWifiApEnabledMethod.invoke(wifiManager)) {
                                }
                                for (Method method1 : wmMethods) {
                                    if (method1.getName().equals("getWifiApState")) {
                                    }
                                }
                            }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (apStatus) {
                                    System.out.println("SUCCESS ");
                                    Toasty.success(getApplicationContext(), "Wifi Hotspot Created").show();
                                    startMessageActivity();
                                } else {
                                    System.out.println("FAILED");
                                    Toasty.error(getApplicationContext(), "Wifi Hotspot Creation Failed").show();
                                }
                            }
                        });

                    } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            return methodFound;

        }
    }

    private class JoinWifiNetwork extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

            WifiManager wifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);

            wifiManager.addNetwork(conf);
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
                wifiManager.startScan();
            }

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    if (i.SSID.toLowerCase().equals(networkSSID.toLowerCase())){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.success(getApplicationContext(), "Joined to " + networkSSID).show();
                                System.out.println("SUCCESS ");
                                startMessageActivity();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.error(GroupShareActivity.this,"Please create group first").show();
                            }
                        });
                    }
                }
            }

//            int netId = wifiManager.addNetwork(conf);
//            Log.d(TAG, "Net Id WifiManager: " + netId);
//            wifiManager.disconnect();
//            boolean state = wifiManager.enableNetwork(netId, true);
//            Log.d(TAG, "Is Connected: " + state);
//            wifiManager.reconnect();


//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toasty.success(getApplicationContext(), "Joined to " + networkSSID).show();
//                    System.out.println("SUCCESS ");
//                    startMessageActivity();
//                }
//            });
            return null;
        }

    }


}
