package com.shareapp.share.transferfiles.config;

import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by NabilurRehman on 9/21/2017.
 */

public class VolleyConfig extends MultiDexApplication {
    public static int TIMEOUT = 15000;
    public static final String TAG = VolleyConfig.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static VolleyConfig mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Pe-icon-7-stroke.ttf");
        mInstance = this;
    }

    public static synchronized VolleyConfig getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
