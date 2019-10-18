package com.shareapp.share.transferfiles.activity;

import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.util.Flash;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class FlashLightActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "AppLog/FlashLightActity";
    private static final String LONG_PRESS = "long_press";
    public static final String WHITE = "white";
    private final Flash flash = new Flash();
    private View background;
    private ToggleButton theButton;
    private Drawable dark;
    private boolean changeColor = false;

    private ImageView ivFlashLight;
    private Boolean isLightOn;
    ActionBar actionBar;


    private AdView mAdView;
    private AdRequest adRequest;


    private SharedPreferences sharedPreferences;

    public class FlashTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return flash.on();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            theButton.setEnabled(true);
            isLightOn = true;
            if (!success) {
                isLightOn = false;
                Toast.makeText(FlashLightActivity.this, "Failed to access camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class WhiteTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return sharedPreferences.getBoolean(WHITE, false);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            changeColor = aBoolean;
            if (changeColor && theButton.isChecked()) {
                background.setBackgroundColor(Color.WHITE);
                ivFlashLight.setImageResource(R.drawable.ic_flashlight);
            } else {
                background.setBackgroundDrawable(dark);
                ivFlashLight.setImageResource(R.drawable.ic_flashlight_s);
            }
        }
    }

    public class PressTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return sharedPreferences.getBoolean(LONG_PRESS, false);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            background.setLongClickable(aBoolean);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_light);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Flash Light");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#151112")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#0c0c0c"));
        }

        mAdView = findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        theButton = (ToggleButton) findViewById(R.id.flashlightButton);
        ivFlashLight = ((ImageView) findViewById(R.id.ivFlashLight));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isLightOn = false;

        //if greater than Marshmellow
//        cam = Camera.open();
//        p = cam.getParameters();
//        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//        cam.setParameters(p);

        ViewParent vp = theButton.getParent();
        if (vp instanceof View) {
            background = (View) vp;
            background.setOnLongClickListener(new LongClickListener());
            dark = background.getBackground();
        } else {
            Log.e(TAG, "Background isn't a view!");
            background = new View(this);
        }

        ImageSpan imageSpan = new ImageSpan(this, R.drawable.power_symbol);
        SpannableString content = new SpannableString("X");
        content.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        theButton.setText(content);
        theButton.setTextOn(content);
        theButton.setTextOff(content);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "Changed pref: " + key);
        switch (key) {
            case LONG_PRESS:
                new PressTask().execute();
                break;
            case WHITE:
                new WhiteTask().execute();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
//            case R.id.action_about:
//                intent = new Intent(this, AboutActivity.class);
//                startActivity(intent);
//                break;
        }
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        if (theButton.isChecked()) {
            theButton.setEnabled(false);
            new FlashTask().execute();
            theButton.setKeepScreenOn(true);
        } else {
            flash.off();
        }

        new PressTask().execute();
        new WhiteTask().execute();
    }


    public void setCamera(Camera camera)
    {
//        cam= camera;
//        if (cam!= null)
//        {
//            mSupportedPreviewSizes = cam.getParameters().getSupportedPreviewSizes();
//            mSupportedFlashModes = cam.getParameters().getSupportedFlashModes();
//            // Set the camera to Auto Flash mode.
//            if (mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH))
//            {
//                Camera.Parameters parameters = cam.getParameters();
//                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                cam.setParameters(parameters);
//            }
//        }
//        requestLayout();
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        flash.close();
    }

    //marshmellow
    private void turnOnFlash(){
//        if (Build.VERSION.SDK_INT >= 21) {
//            cam.startPreview();
//        } else {
//            // Implement this feature without material design
//        }
    }


    public void onFlashToggleClicked(View v) {
        boolean bool = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (bool) {
            if (!isLightOn) {
                new FlashTask().execute();
                v.setKeepScreenOn(true);
                ivFlashLight.setImageResource(R.drawable.ic_flashlight);
                isLightOn = true;
//                turnOnFlash();
//            if (changeColor) {
//                background.setBackgroundColor(Color.WHITE);
//            }
            } else {
                flash.off();
                v.setKeepScreenOn(false);
                ivFlashLight.setImageResource(R.drawable.ic_flashlight_s);
                isLightOn = false;
//            if (background != null) {
//                background.setBackgroundDrawable(dark);
//            }
            }
        } else {
            Toasty.error(this,"Sorry! we haven,t found flashlight");
        }
    }

    public void onToggleClicked(View v) {
        if (theButton.isChecked()) {
            new FlashTask().execute();
            v.setKeepScreenOn(true);
            ivFlashLight.setImageResource(R.drawable.ic_flashlight_s);
            if (changeColor) {
                background.setBackgroundColor(Color.WHITE);
            }
        } else {
            flash.off();
            v.setKeepScreenOn(false);
            ivFlashLight.setImageResource(R.drawable.ic_flashlight_s);
            if (background != null) {
                background.setBackgroundDrawable(dark);
            }
        }
    }

    public class LongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            theButton.setChecked(!theButton.isChecked());
            onToggleClicked(theButton);
            return true;
        }
    }
}
