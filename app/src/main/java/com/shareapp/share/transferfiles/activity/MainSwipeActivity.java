package com.shareapp.share.transferfiles.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.shareapp.share.transferfiles.R;

import com.shareapp.share.transferfiles.adapters.TabsPagerAdapter;
import com.shareapp.share.transferfiles.config.ReqeustKey;
import com.shareapp.share.transferfiles.transfer.TransferService;
import com.shareapp.share.transferfiles.widget.FilesCart;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainSwipeActivity extends AppCompatActivity implements ActionBar.TabListener {

    private final static String TAG = "AppLog/MainSwipeActvty";

    private ViewPager viewPager;
    private ActionBar actionBar;
    private TabsPagerAdapter tabsPagerAdapter;

    private FilesCart filesCart;

    private Button bSend;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main_tab);

        filesCart = new FilesCart(this);

        bSend = (Button) findViewById(R.id.bSend);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
// Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

//        bSend = (Button) findViewById(R.id.bSend);

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(tabsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

    }


    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        int position = tab.getPosition();
        viewPager.setCurrentItem(position);
        Log.v(TAG, "Tab Position: " + position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bSend.setText("Send (" + filesCart.getQuantity() + ")");
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
                if (filesCart.addFile(path)) {
                    bSend.setText("Send (" + filesCart.getQuantity() + ")");
                    Toasty.success(getApplicationContext(), "File Added").show();
                }
            } else {
                Log.e(TAG, "No File Selected");
                Toasty.info(this, "No File Selected").show();
            }
        } else {
            Toasty.info(this, "Request code not matched").show();
        }
    }

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
