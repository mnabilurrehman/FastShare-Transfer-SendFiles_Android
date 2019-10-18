package com.shareapp.share.transferfiles.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.activity.HotspotReceiveActivity;
import com.shareapp.share.transferfiles.activity.MainSwipeActivity;
import com.shareapp.share.transferfiles.adapters.SliderImagesRecyclerAdapter;
import com.shareapp.share.transferfiles.adapters.VideoAndAdRecyclrAdapter;
import com.shareapp.share.transferfiles.config.ReqeustKey;
import com.shareapp.share.transferfiles.config.VolleyConfig;
import com.shareapp.share.transferfiles.domain.Library;
import com.shareapp.share.transferfiles.domain.Video;
import com.shareapp.share.transferfiles.model.Model_images;
import com.shareapp.share.transferfiles.service.task.GetYouTubeUserVideosTask;
import com.shareapp.share.transferfiles.util.GPSTracker;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.neovisionaries.i18n.CountryCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class YoutubeVideoFragment extends Fragment {

    private static final String TAG = "AppLog/YutubeVdeoFgmnt";

    private static final String youtubeLink = "";

//    private VideosListView listView;

    private Button bReceive;
    private Button bSend;

    private ImageView ivNoInternet;

    private GPSTracker gpsTracker;

    private CountryCode[] countryCode;

    private ImageView ivCross;

//    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    //Slider
    public static ArrayList<Model_images> al_images = new ArrayList<>();
    private ArrayList<Model_images> imagesPath = new ArrayList<>();
    //    SliderImagesAdapter obj_adapter;
    boolean boolean_folder;
    private LinearLayout llImageSlider;

    RecyclerView recycler_YoutubeVideosListView;
    VideoAndAdRecyclrAdapter adapterAd;
    //    RecyclerView.LayoutManager ImageSliderLayoutManager;
    LinearLayoutManager ImageSliderLayoutManager;

    RecyclerView recyclerViewImageSlider;
    RecyclerView.Adapter adapter;

    //end Slider

    public YoutubeVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_youtube_video, container, false);

//        AdView adView = new AdView(getActivity());
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

//        mAdView = view.findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        gpsTracker = new GPSTracker(getActivity());

        countryCode = CountryCode.values();
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.sample_ad_interstitial));

        //slider
        llImageSlider = (LinearLayout) view.findViewById(R.id.llImageSlider);


        recycler_YoutubeVideosListView = (RecyclerView) view.findViewById(R.id.recycler_videosListView);


        // Passing the column number 1 to show online one column in each row.
        LinearLayoutManager layoutManagera
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        ImageSliderLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        try {
//            recycler_YoutubeVideosListView .setLayoutManager(layoutManagera);
            recycler_YoutubeVideosListView.setLayoutManager(layoutManagera);

        } catch (Exception e) {
            Log.e(TAG, "layout manager exception: " + e.getMessage());
        }

        if (isOnline())
            getUserYouTubeFeed();

        recyclerViewImageSlider = (RecyclerView) view.findViewById(R.id.recycler_view);

        // Passing the column number 1 to show online one column in each row.
//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);


        try {
//            recyclerViewImageSlider.setLayoutManager(new GridLayoutManager(getContext(), 4));
            recyclerViewImageSlider.setLayoutManager(ImageSliderLayoutManager);
        } catch (Exception e) {
            Log.e(TAG, "layout manager exception: " + e.getMessage());
        }

        //end slider
        ivCross = (ImageView) view.findViewById(R.id.ivCross);
        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llImageSlider.setVisibility(View.GONE);
            }
        });
//        gridView.setAdapter(adapter);
        //end slider/
        Log.i(TAG, "YoutubeVideoFragment");


        bReceive = view.findViewById(R.id.bReceive);
        bSend = view.findViewById(R.id.bSender);

        ivNoInternet = (ImageView) view.findViewById(R.id.ivNoInternet);
        ivNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                    getUserYouTubeFeed();
                    ivNoInternet.setVisibility(View.GONE);
//                    listView.setVisibility(View.VISIBLE);
                } else {
                    ivNoInternet.setVisibility(View.VISIBLE);
//                    listView.setVisibility(View.GONE);
                    Toasty.info(getActivity(), "Please connect to internet").show();
                }
            }
        });

        init();

//        listView = (VideosListView) view.findViewById(R.id.videosListView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        if (!mInterstitialAd.isLoaded()) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
        if (isOnline()) {
            getUserYouTubeFeed();
            ivNoInternet.setVisibility(View.GONE);
//            listView.setVisibility(View.VISIBLE);
        } else {
            ivNoInternet.setVisibility(View.VISIBLE);
//            listView.setVisibility(View.GONE);
            Toasty.info(getActivity(), "Please connect to internet").show();
        }

    }


    private void init() {

        bReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "bReceiveClicked");
//                mInterstitialAd.setAdListener(new AdListener() {
//                    @Override
//                    public void onAdLoaded() {
//                        // Code to be executed when an ad finishes loading.
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(int errorCode) {
//                        // Code to be executed when an ad request fails.
//                        Log.i(TAG, "onAdFailedToLoad");
////                        if (!mInterstitialAd.isLoaded()) {
////                            startReceiveHotspotActivity();
////                        }
//                    }
//
//                    @Override
//                    public void onAdOpened() {
//                        // Code to be executed when the ad is displayed.
//                    }
//
//                    @Override
//                    public void onAdLeftApplication() {
//                        // Code to be executed when the user has left the app.
//                    }
//
//                    @Override
//                    public void onAdClosed() {
//                        // Code to be executed when when the interstitial ad is closed.
//                        startReceiveHotspotActivity();
//                    }
//
//                });
//                if (mInterstitialAd.isLoaded()) {
////                    mInterstitialAd.show();
//                } else {
//                    Log.i(TAG, "The interstitial wasn't loaded yet.");
                    startReceiveHotspotActivity();
//                }
            }
        });

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "bSendClicked");
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Code to be executed when an ad request fails.
                        Log.i(TAG, "onAdFailedToLoad");
//                        if (!mInterstitialAd.isLoaded()) {
//                            startMainSwipeactivity();
//                        }
                    }

                    @Override
                    public void onAdOpened() {
                        // Code to be executed when the ad is displayed.
                        Log.i(TAG, "onAdOpened");
                    }

                    @Override
                    public void onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                        Log.i(TAG, "onAdLeftApplication");
                    }

                    @Override
                    public void onAdClosed() {
                        // Code to be executed when when the interstitial ad is closed.
                        Log.i(TAG, "onAdClosed");
                        startMainSwipeactivity();
                    }
                });
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.i(TAG, "The interstitial wasn't loaded yet.");
                    startMainSwipeactivity();
                }
            }
        });

        if ((ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) &&
                    (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        ReqeustKey.STORAGE);
            }
        } else {
            Log.e("Else", "Else");
//            fn_imagespath();

//            adapter = new SliderImagesRecyclerAdapter(getContext(), fn_imagespath());
            adapter = new SliderImagesRecyclerAdapter(getContext(), getImageBuckets(getContext()));
            recyclerViewImageSlider.setAdapter(adapter);

        }

    }

    private void startMainSwipeactivity() {
        Intent nextActivity = new Intent(getActivity(), MainSwipeActivity.class);
        startActivity(nextActivity);
    }

    private void startReceiveHotspotActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(getContext())) {
                Intent nextActivity = new Intent(getActivity(), HotspotReceiveActivity.class);
                startActivity(nextActivity);

            } else {
                goToSettings();
            }
        }
    }

    private void showInterstitialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.i(TAG, "The interstitial wasn't loaded yet.");
        }
    }

    private void goToSettings() {
        Toasty.info(getContext(), "Please allow to change system settings \n APP INFO > ADVANCED", 200000).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getContext().getPackageName()));
                getActivity().startActivityForResult(intent, ReqeustKey.OPEN_SETTINGS);
            }
        }
    }

    // This is the XML onClick listener to retreive a users video feed
    public void getUserYouTubeFeed() {
        Log.i(TAG, "getUserYouTubeFeed");
        // We start a new task that does its work on its own thread
        // We pass in a handler that will be called when the task has finished
        // We also pass in the name of the user we are searching YouTube for


        if (!gpsTracker.canGetLocation()) {
//            gpsTracker.showSettingsAlert();
//            new GetYouTubeUserVideosTask(responseHandler, 0.0 + "", 0.0 + "", "US", responseHandler).run();
            Log.i(TAG, "Location Service Disabled");
            callYoutubeVideoAPI(0.0 + "", 0.0 + "", "US");
        } else {
            Log.i(TAG, "Location Service available");
            String countryName = getAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            Log.i(TAG, "LatLng: " + gpsTracker.getLatitude() + "," + gpsTracker.getLongitude());
            String countryCode = countryCode(countryName);
//            new GetYouTubeUserVideosTask(responseHandler, gpsTracker.getLatitude() + "", gpsTracker.getLongitude() + "", countryCode, responseHandler).run();
            callYoutubeVideoAPI(gpsTracker.getLatitude() + "", gpsTracker.getLongitude() + "", countryCode);
        }
    }

    private String countryCode(String countryName) {
        for (CountryCode count : countryCode) {
            if (count.getName().contains(countryName)) {
                return count.getAlpha2();
            }
        }
        return "US";
    }

    // This is the handler that receives the response when the YouTube task has finished
    Handler responseHandler = new Handler() {
        public void handleMessage(Message msg) {
            populateListWithVideos(msg);
        }
    };

    /**
     * This method retrieves the Library of videos from the task and passes them to our ListView
     *
     * @param msg
     */
    private void populateListWithVideos(Message msg) {
        // Retreive the videos are task found from the data bundle sent back
        Library lib = (Library) msg.getData().get(GetYouTubeUserVideosTask.LIBRARY);


        Log.i(TAG, "populateListWithVideos" + lib.getVideos());

        // Because we have created a custom ListView we don't have to worry about setting the adapter in the activity
        // we can just call our custom method with the list of items we want to display
//        listView.setVideos(lib.getVideos());
    }

    @Override
    public void onStop() {
        responseHandler = null;
        super.onStop();
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String countryName = "United States";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (!addresses.isEmpty()) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                countryName = add.substring(add.lastIndexOf(",") + 2, add.length());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
//            Toasty.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return countryName;
    }


    public void callYoutubeVideoAPI(final String lat, final String lng, final String countryCode) {
        Log.i(TAG, "callYoutubeVideoAPI");
        String tag_string_req = "req_post_job";


        StringRequest strReq = new StringRequest(Request.Method.GET,
                "https://www.googleapis.com/youtube/v3/videos?chart=mostpopular&regionCode=" + countryCode + "&location=" + lat + "," + lng + "&locationRadius=50km&videoDefinition=high&key=AIzaSyCNTulSWUQGwhpAszY3eEzLD642ocITLTw&maxResults=20&part=snippet",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "URL_POST_JOB: " + response.toString());


                        try {
                            JSONObject jObj = new JSONObject(response);

                            Log.i(TAG, "URL_POST_JOB Response: \n" + jObj);
//                    boolean error = jObj.getBoolean("error");
                            // Check for error node in json

//                        stopWaitLoading();
                            JSONArray jsonArray = jObj.getJSONArray("items");
                            if (jsonArray != null && jsonArray.length() > 0) {
//                                Log.i(TAG, "Youtube: " + jsonArray);

                                // Create a list to store are videos in
                                List<Video> videos = new ArrayList<Video>();
                                // Loop round our JSON list of videos creating Video objects to use within our app
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            Log.i(TAG, "JSON Object " + jsonObject.toString());
                                    JSONObject snippet = jsonObject.getJSONObject("snippet");
                                    JSONObject localized = snippet.getJSONObject("localized");
                                    String channelTitle = snippet.getString("channelTitle");
                                    // The title of the video
                                    String title = localized.getString("title");
                                    // The url link back to YouTube, this checks if it has a mobile url
                                    // if it doesnt it gets the standard url
                                    String url;

                                    //youtube video id
                                    url = jsonObject.getString("id");
//                            try {
//                                url = jsonObject.getJSONObject("player").getString("mobile");
//                            } catch (JSONException ignore) {
//                                url = jsonObject.getJSONObject("player").getString("default");
//                            }
                                    // A url to the thumbnail image of the video
                                    // We will use this later to get an image using a Custom ImageView
                                    // Found here http://blog.blundell-apps.com/imageview-with-loading-spinner/
//                            String thumbUrl = jsonObject.getJSONObject("thumbnail").getString("sqDefault");
                                    String thumbUrl = null;

                                    try {
                                        thumbUrl = snippet.getJSONObject("thumbnails").getJSONObject("maxres").getString("url");
                                    } catch (Exception e) {
                                        try {
                                            thumbUrl = snippet.getJSONObject("thumbnails").getJSONObject("standard").getString("url");
                                        } catch (Exception e1) {
                                            try {
                                                thumbUrl = snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");
                                            } catch (Exception e2) {
                                                try {
                                                    thumbUrl = snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                                                } catch (Exception e3) {
                                                    try {
                                                        thumbUrl = snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");
                                                    } catch (Exception e4) {

                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Create the video object and add it to our list
                                    videos.add(new Video(title, url, thumbUrl, channelTitle));
                                }

//                                listView.setVideos(videos);
                                adapterAd = new VideoAndAdRecyclrAdapter(videos, getContext());

                                Log.d(TAG, "Video Ad list count: " + adapterAd.getItemCount() + "  " + videos.size());
                                recycler_YoutubeVideosListView.setAdapter(adapterAd);

                            } else {
//                        stopWaitLoading();
                                // Error in update. Get the error message
                                String errorMsg = jObj.getString("message");
//                        ToastNotificationHandler.showLong(MainActivity.this, "Something went wrong!");
                            }
                        } catch (JSONException e) {
//                    stopWaitLoading();
                            // JSON error
                            e.printStackTrace();
//                    if (!MasterConfig.isLiveServer())
                            Log.i(TAG, "Json error: " + e.getMessage());
//                    ToastNotificationHandler.showLong(YoutubeVideoFragment.this, getString(R.string.api_app_side_error));


                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                stopWaitLoading();
//                if (!MasterConfig.isLiveServer())
                Log.e(TAG, "URL_POST_JOB Error: " + error);

                if (error instanceof ServerError) {
//                    ToastNotificationHandler.showLong(MainActivity.this, getString(R.string.api_server_error));
                } else {
//                    ToastNotificationHandler.showLong(MainActivity.this, getString(R.string.api_internet_error));
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyConfig.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public ArrayList<Model_images> fn_imagespath() {
        al_images.clear();
//        al_images =  new ArrayList<>();
        boolean_folder = false;

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor != null && cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.i(TAG, "coloumn :: " + absolutePathOfImage);
            Log.i(TAG, "Folder:  " + cursor.getString(column_index_folder_name));

            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }

            if (boolean_folder) {
                ArrayList<String> al_path = new ArrayList<>();
//                Log.i(TAG, "int_position: " + int_position);
//                Log.i(TAG, "al_images size: " + al_images.size());
//                Log.i(TAG, "al_path size: " + al_path.size());
//                if (al_images.size() == 0) {
//
                //                } else {

                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_path.add(absolutePathOfImage);
                al_images.get(int_position).setAl_imagepath(al_path);
//                }


            } else {
//                if (cursor.getCount() != 0) {
//
//                }
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setAl_imagepath(al_path);
                obj_model.getStr_folder();
                Log.i(TAG, "Cursor Count: " + cursor.getCount());

                al_images.add(obj_model);

            }
        }

//        for (int i = 0; i < al_images.size(); i++) {
////            Log.i(TAG, al_images.get(i).getStr_folder());
//            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
////                Log.i(TAG, al_images.get(i).getAl_imagepath().get(j));
//            }
//        }
//        obj_adapter = new SliderImagesAdapter(getContext(), al_images);

        //recycler view
//        for (String s : al_images.get(1).getAl_imagepath()) {
//            imagesPath.add(s);
//        }

//        adapter = new SliderImagesRecyclerAdapter(getContext(), al_images);
//        recyclerViewImageSlider.setAdapter(adapter);
//        ImageSliderLayoutManager = new GridLayoutManager(getContext(), obj_adapter.getCount());
        return al_images;
    }

    public static ArrayList<String> getImageBuckets(Context mContext) {
        ArrayList<String> buckets = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            File file;
            while (cursor.moveToNext()) {
//                String bucketPath = cursor.getString(cursor.getColumnIndex(projection[0]));
                String fisrtImage = cursor.getString(cursor.getColumnIndex(projection[1]));
                file = new File(fisrtImage);
                if (file.exists() && !buckets.contains(file.getPath())) {
                    buckets.add(file.getPath());
                }
//                if (buckets.size() ==19)
            }
            cursor.close();
        }
        return buckets;
    }

}
