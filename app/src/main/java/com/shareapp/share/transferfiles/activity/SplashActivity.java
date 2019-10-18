package com.shareapp.share.transferfiles.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.config.ReqeustKey;
import com.shareapp.share.transferfiles.tutorial.TutorialOneActivity;
import com.shareapp.share.transferfiles.util.TutorialHandler;

import es.dmoral.toasty.Toasty;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "AppLog/SplashActivity";
    private int progressStatus = 0;

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        adRequest = new AdRequest.Builder().build();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.sample_ad_interstitial));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#009BDD"));
        }

        Toasty.Config.getInstance().apply();
        checkLocationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mInterstitialAd.isLoaded()) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }

    }

    private void startNextActivity() {
        Intent nextActivity;
        boolean tutorialState = new TutorialHandler(this).getTutorialState();
        if (tutorialState) {
            nextActivity = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            nextActivity = new Intent(SplashActivity.this, TutorialOneActivity.class);
        }
        startActivity(nextActivity);

        finish();
    }

    private void init() {

        Log.i(TAG, "bReceiveClicked");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.d(TAG, "onAdFailedToLoad");
//                        if (!mInterstitialAd.isLoaded()) {
//                            startReceiveHotspotActivity();
//                        }
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                startNextActivity();
            }

        });
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d(TAG, "The interstitial wasn't loaded yet.");
            startNextActivity();
        }

    }

    private void checkLocationPermission() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        ReqeustKey.STORAGE);
            } else {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            ReqeustKey.FINE_LOCATION);
                } else {
//                    displayLocationSettingsRequest(this);
                    requestCameraPermission();
                }
            }
        } else {
            startActivity();
        }
    }

    private void startActivity() {
        Log.d(TAG, "startActivity");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 3000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ReqeustKey.FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity();
                } else {
                    Toasty.info(this, "Location permission will give customized experience").show();
                    requestCameraPermission();
                }
                break;
            case ReqeustKey.STORAGE:
                if (grantResults.length > 0)
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        checkLocationPermission();
                    } else {
                        Toasty.error(this, "Please allow permission to use the app").show();
                        this.finishAffinity();
                    }
                break;
            case ReqeustKey.COARSE_LOCATION:
                startActivity();
                break;
            case ReqeustKey.CAMERA_FLASHLIGHT:
                startActivity();
                break;
            default:
                break;
        }
        //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestCameraPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        ReqeustKey.CAMERA_FLASHLIGHT);
            } else {
                startActivity();
            }
        }
    }


//    private void displayLocationSettingsRequest(Context context) {
//        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
//                .addApi(LocationServices.API).build();
//        googleApiClient.connect();
//
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(10000 / 2);
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);
//
//        final Status[] status = new Status[1];
//
//        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                status[0] = result.getStatus();
//                switch (status[0].getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        Log.i(TAG, "All location settings are satisfied.");
//                        startActivity();
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
//                        startActivity();
//                        try {
//                            // Show the dialog by calling startResolutionForResult(), and check the result
//                            // in onActivityResult().
//                            status[0].startResolutionForResult(SplashActivity.this, ReqeustKey.COARSE_LOCATION);
//                        } catch (IntentSender.SendIntentException e) {
//                            Log.i(TAG, "PendingIntent unable to execute request.");
//
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
//                        startActivity();
//                        break;
//                }
//            }
//        });
//    }
}
