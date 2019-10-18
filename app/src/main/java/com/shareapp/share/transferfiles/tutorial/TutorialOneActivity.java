package com.shareapp.share.transferfiles.tutorial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.activity.MainActivity;
import com.shareapp.share.transferfiles.util.TutorialHandler;

public class TutorialOneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_one);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#034d8f"));
        }

    }

    public void bImageClicked(View view) {
        Intent nextActivity =  new Intent(this, TutorialTwoActivity.class);
        startActivity(nextActivity);
    }

    public void skipTutorialClicked(View view) {
        new TutorialHandler(this).setTutorialState(true);
        Intent nextActivity =  new Intent(this, MainActivity.class);
        nextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(nextActivity);
    }
}
