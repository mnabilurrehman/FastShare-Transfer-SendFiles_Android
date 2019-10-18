package com.shareapp.share.transferfiles.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.adapters.FilesReceivedTabAdapter;
import com.shareapp.share.transferfiles.config.ReqeustKey;
import com.shareapp.share.transferfiles.transfer.TransferService;
import com.shareapp.share.transferfiles.util.Settings;
import com.shareapp.share.transferfiles.widget.FilesCart;
import com.shareapp.share.transferfiles.widget.ReceivedFiles;
import com.google.android.gms.ads.MobileAds;


import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "AppLog/MainActivity";

    private ReceivedFiles receivedFiles;

    ActionBar actionBar;

    private TextView tvHome;
    private TextView tvAudio;
    private TextView tvVideo;
    private TextView tvApps;
    private TextView tvPhotos;
    private TextView tvFiles;

    private ImageView ivHome;
    private ImageView ivAudio;
    private ImageView ivApps;
    private ImageView ivVideo;
    private ImageView ivPhotos;
    private ImageView ivFiles;

    private LinearLayout llFooter;

    private Button bSend;

    private ViewPager viewPager;

    private FilesReceivedTabAdapter filesReceivedTabAdapter;

    private ViewGroup.LayoutParams params;

    private FilesCart filesCart;

    private MaterialDialog.Builder builder;

//    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, getString(R.string.sample_ad_id));

//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getString(R.string.sample_ad_interstitial));

        createShareDataDir();

        actionBar = getSupportActionBar();




        receivedFiles = new ReceivedFiles();
        filesCart = new FilesCart(getApplicationContext());
        filesCart.destroyCart();

        tvHome = (TextView) findViewById(R.id.tvHome);
        tvAudio = (TextView) findViewById(R.id.tvAudio);
        tvApps = (TextView) findViewById(R.id.tvApps);
        tvVideo = (TextView) findViewById(R.id.tvVideo);
        tvPhotos = (TextView) findViewById(R.id.tvPhotos);
        tvFiles = (TextView) findViewById(R.id.tvFiles);

        ivHome = (ImageView) findViewById(R.id.ivHome);
        ivAudio = (ImageView) findViewById(R.id.ivAudio);
        ivApps = (ImageView) findViewById(R.id.ivApps);
        ivVideo = (ImageView) findViewById(R.id.ivVideo);
        ivPhotos = (ImageView) findViewById(R.id.ivPhotos);
        ivFiles = (ImageView) findViewById(R.id.ivFiles);

        llFooter = (LinearLayout) findViewById(R.id.llFotter);

        bSend = (Button) findViewById(R.id.bSend);

        filesReceivedTabAdapter = new FilesReceivedTabAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(0);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setTabButton(position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(filesReceivedTabAdapter);

        initFeedBackDialog();

    }

    private void gotoPlayStore() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }


    private void initFeedBackDialog() {
        builder = new MaterialDialog.Builder(this)
                .title("Rate Us")
                .content("Would you like to rate us?")
                .positiveText("Yes")
                .negativeText("No")
                .neutralText("Exit");


        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                // TODO
                gotoPlayStore();
            }
        })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        // TODO
                        dialog.dismiss();
                    }
                });
    }


    //To create ShareData folder at the start so to avoid null pointer exception
    private void createShareDataDir() {
        File root = Environment.getExternalStorageDirectory();
        File download = new File(root, "Download");
        File dir = new File(download, "ShareData");

        try {
            if (dir.mkdir()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory is not created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setItemsReceived();
        registerReceiver(mMessageReceiver, new IntentFilter("update_main_activity_footer"));

        if (new Settings(this).getBoolean(Settings.Key.BEHAVIOR_RECEIVE)) {
            TransferService.startStopService(this, true);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mMessageReceiver);
    }

    private void setItemsReceived() {

        int audioCount = receivedFiles.getAudioFilesCount();
        int videoCount = receivedFiles.getVideoFilesCount();
        int imageCount = receivedFiles.getImagesFilesCount();
        int othersCount = receivedFiles.getOtherFilesCount();

        if (audioCount == 0) {
            tvAudio.setText("Audio");
        } else {
            tvAudio.setText("Audio (" + audioCount + ")");
        }

        if (videoCount == 0) {
            tvVideo.setText("Video");
        } else {
            tvVideo.setText("Video (" + videoCount + ")");
        }

        if (imageCount == 0) {
            tvApps.setText("Photo");
        } else {
            tvApps.setText("Photo (" + imageCount + ")");
        }

        if (othersCount == 0) {
            tvPhotos.setText("Others");
        } else {
            tvPhotos.setText("Others (" + othersCount + ")");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            default:
                break;
        }


//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        TransferService.startStopService(this, false);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReqeustKey.OPEN_SETTINGS) {
            if (requestCode == Activity.RESULT_OK) {
                Intent nextActivity = new Intent(MainActivity.this, HotspotReceiveActivity.class);
                startActivity(nextActivity);
            } else {
                Toasty.error(getApplicationContext(), "Please allow to change system settings");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history, menu);
        return true;
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_history:
                Intent gotoHistoryActivity = new Intent(this, HistoryActivity.class);
                startActivity(gotoHistoryActivity);
                break;
            case R.id.action_group_share:
                startGroupShareActivity();
                break;
            case R.id.action_invite:
//                shareApplication();
                Toasty.info(this, "Invite others by sharing APK file with others").show();
                Intent gotoInviteActivity = new Intent(this, InviteActivity.class);
                startActivity(gotoInviteActivity);
                break;
            case R.id.action_flashlight:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent gotoFlashLightAPI2Activity = new Intent(this, FlashLightAPI2Activity.class);
                    startActivity(gotoFlashLightAPI2Activity);
                } else {
                    Intent gotoFlashLightActivity = new Intent(this, FlashLightActivity.class);
                    startActivity(gotoFlashLightActivity);
                }

                break;
            default:
                break;
        }
        return true;
        //        return super.onOptionsItemSelected(item);
    }

    private void startGroupShareActivity() {
        Intent gotoGroupShareActivity = new Intent(this, GroupShareActivity.class);
        startActivity(gotoGroupShareActivity);
    }

    private void shareApplication() {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        intent.setType("*/*");


        // Append file and send Intent
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        startActivity(Intent.createChooser(intent, "Share app via"));
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");

            Log.v(TAG, "Broadcast Received");
//
//            filesCart = new FilesCart(getApplicationContext());
//            setItemsReceived();

            //do other stuff here
        }
    };

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            showFeedBackDialog();
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    private void showFeedBackDialog() {
        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    public void bHomeClicked(View view) {
        viewPager.setCurrentItem(0);
    }

    public void bAudioClicked(View view) {
        viewPager.setCurrentItem(1);
    }

    public void bVideoClicked(View view) {
        viewPager.setCurrentItem(2);
    }

    public void bPhotosClicked(View view) {
        viewPager.setCurrentItem(3);
    }

    public void bOthersClicked(View view) {
        viewPager.setCurrentItem(4);
    }

    public void bFilesClicked(View view) {
        viewPager.setCurrentItem(5);
    }

    public void bGroupShareClicked(View view) {
        startGroupShareActivity();
    }

    private void setTabButton(int position) {
        switch (position) {
            case 0:
//                params = llFooter.getLayoutParams();
//                params.height = params.height / 2;
//                llFooter.setLayoutParams(params);
//                bSend.setVisibility(View.GONE);

                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00AFF0")));
                bSend.setBackgroundColor(Color.parseColor("#00AFF0"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.parseColor("#009BDD"));
                }

                tvHome.setTextColor(Color.parseColor("#00AFF0"));
                tvAudio.setTextColor(Color.parseColor("#333333"));
                tvApps.setTextColor(Color.parseColor("#333333"));
                tvVideo.setTextColor(Color.parseColor("#333333"));
                tvPhotos.setTextColor(Color.parseColor("#333333"));
                tvFiles.setTextColor(Color.parseColor("#333333"));

                ivHome.setImageResource(R.drawable.home_ic_s);
                ivAudio.setImageResource(R.drawable.audio_ic);
                ivApps.setImageResource(R.drawable.app_ic);
                ivVideo.setImageResource(R.drawable.video_ic);
                ivPhotos.setImageResource(R.drawable.photo_ic);
                ivFiles.setImageResource(R.drawable.files_ic);
                break;
            case 1:
//                params = llFooter.getLayoutParams();
//                params.height = params.height * 2;
//                llFooter.setLayoutParams(params);
//                bSend.setVisibility(View.VISIBLE);
//                showInterstitialAd();

                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009e64")));
                bSend.setBackgroundColor(Color.parseColor("#009e64"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.parseColor("#008049"));
                }

                tvHome.setTextColor(Color.parseColor("#333333"));
                tvAudio.setTextColor(Color.parseColor("#009e64"));
                tvApps.setTextColor(Color.parseColor("#333333"));
                tvVideo.setTextColor(Color.parseColor("#333333"));
                tvPhotos.setTextColor(Color.parseColor("#333333"));
                tvFiles.setTextColor(Color.parseColor("#333333"));

                ivHome.setImageResource(R.drawable.home_ic);
                ivAudio.setImageResource(R.drawable.audio_ic_s);
                ivApps.setImageResource(R.drawable.app_ic);
                ivVideo.setImageResource(R.drawable.video_ic);
                ivPhotos.setImageResource(R.drawable.photo_ic);
                ivFiles.setImageResource(R.drawable.files_ic);
                break;
            case 2:
//                params = llFooter.getLayoutParams();
//                params.height = params.height * 2;
//                llFooter.setLayoutParams(params);
//                bSend.setVisibility(View.VISIBLE);


                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0bbdae")));
                bSend.setBackgroundColor(Color.parseColor("#0bbdae"));//0bbdae
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.parseColor("#00ae9e"));//00ae9e
                }

                tvHome.setTextColor(Color.parseColor("#333333"));
                tvAudio.setTextColor(Color.parseColor("#333333"));
                tvApps.setTextColor(Color.parseColor("#0bbdae"));
                tvVideo.setTextColor(Color.parseColor("#333333"));
                tvPhotos.setTextColor(Color.parseColor("#333333"));
                tvFiles.setTextColor(Color.parseColor("#333333"));

                ivHome.setImageResource(R.drawable.home_ic);
                ivAudio.setImageResource(R.drawable.audio_ic);
                ivApps.setImageResource(R.drawable.app_ic_s);
                ivVideo.setImageResource(R.drawable.video_ic);
                ivPhotos.setImageResource(R.drawable.photo_ic);
                ivFiles.setImageResource(R.drawable.files_ic);
                break;
            case 3:
//                params = llFooter.getLayoutParams();
//                params.height = params.height * 2;
//                llFooter.setLayoutParams(params);
//                bSend.setVisibility(View.VISIBLE);
//                showInterstitialAd();

                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c9283e")));
                bSend.setBackgroundColor(Color.parseColor("#c9283e"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.parseColor("#a80027"));
                }

                tvHome.setTextColor(Color.parseColor("#333333"));
                tvAudio.setTextColor(Color.parseColor("#333333"));
                tvApps.setTextColor(Color.parseColor("#333333"));
                tvVideo.setTextColor(Color.parseColor("#c9283e"));
                tvPhotos.setTextColor(Color.parseColor("#333333"));
                tvFiles.setTextColor(Color.parseColor("#333333"));

                ivHome.setImageResource(R.drawable.home_ic);
                ivAudio.setImageResource(R.drawable.audio_ic);
                ivApps.setImageResource(R.drawable.app_ic);
                ivVideo.setImageResource(R.drawable.video_ic_s);
                ivPhotos.setImageResource(R.drawable.photo_ic);
                ivFiles.setImageResource(R.drawable.files_ic);
                break;
            case 4:
//                params = llFooter.getLayoutParams();
//                params.height = params.height * 2;
//                llFooter.setLayoutParams(params);
//                bSend.setVisibility(View.VISIBLE);


                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#e88554")));
                bSend.setBackgroundColor(Color.parseColor("#e88554"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.parseColor("#d06c40"));
                }

                tvHome.setTextColor(Color.parseColor("#333333"));
                tvAudio.setTextColor(Color.parseColor("#333333"));
                tvApps.setTextColor(Color.parseColor("#333333"));
                tvVideo.setTextColor(Color.parseColor("#333333"));
                tvPhotos.setTextColor(Color.parseColor("#e88554"));
                tvFiles.setTextColor(Color.parseColor("#333333"));

                ivHome.setImageResource(R.drawable.home_ic);
                ivAudio.setImageResource(R.drawable.audio_ic);
                ivApps.setImageResource(R.drawable.app_ic);
                ivVideo.setImageResource(R.drawable.video_ic);
                ivPhotos.setImageResource(R.drawable.photo_ic_ss);
                ivFiles.setImageResource(R.drawable.files_ic);
                break;
            case 5:
//                params = llFooter.getLayoutParams();
//                params.height = params.height * 2;
//                llFooter.setLayoutParams(params);
//                bSend.setVisibility(View.VISIBLE);
//                showInterstitialAd();

                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#014873")));
                bSend.setBackgroundColor(Color.parseColor("#014873"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.parseColor("#0e2d5a"));
                }

                tvHome.setTextColor(Color.parseColor("#333333"));
                tvAudio.setTextColor(Color.parseColor("#333333"));
                tvApps.setTextColor(Color.parseColor("#333333"));
                tvVideo.setTextColor(Color.parseColor("#333333"));
                tvPhotos.setTextColor(Color.parseColor("#333333"));
                tvFiles.setTextColor(Color.parseColor("#014873"));

                ivHome.setImageResource(R.drawable.home_ic);
                ivAudio.setImageResource(R.drawable.audio_ic);
                ivApps.setImageResource(R.drawable.app_ic);
                ivVideo.setImageResource(R.drawable.video_ic);
                ivPhotos.setImageResource(R.drawable.photo_ic);
                ivFiles.setImageResource(R.drawable.files_ic_s);
                break;

        }
    }

    public void imageSliderClicked(View view) {
        Toasty.info(this,"Select photos and click send button").show();
        viewPager.setCurrentItem(4);
    }

//    private void showInterstitialAd() {
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        } else {
//            Log.d("TAG", "The interstitial wasn't loaded yet.");
//        }
//    }

    public void bSendClicked(View view) {
        if (filesCart.getQuantity() == 0) {
            Toasty.info(getApplicationContext(), "Please select file to send").show();
        } else {
            ArrayList<Uri> uriArrayList = new ArrayList<Uri>();
            for (String path : filesCart.getFileLink()) {
                Uri uri = Uri.fromFile(new File(path));
                uriArrayList.add(uri);
            }

            Intent shareIntent = new Intent(this, ShareActivity.class);
            shareIntent.setAction("android.intent.action.SEND_MULTIPLE");
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList);
            startActivity(shareIntent);
//        this.startActivityForResult(shareIntent, ReqeustKey.SHARE_FILE_REQUEST);
        }
    }
}

