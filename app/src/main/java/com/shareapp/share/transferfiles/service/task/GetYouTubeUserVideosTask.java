package com.shareapp.share.transferfiles.service.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.shareapp.share.transferfiles.domain.Library;
import com.shareapp.share.transferfiles.util.StreamUtils;

import com.shareapp.share.transferfiles.domain.Video;

public class GetYouTubeUserVideosTask implements Runnable {
    private final static String TAG = "AppLog/GetYouTubeUsrV";

    // A reference to retrieve the data when this task finishes
    public static final String LIBRARY = "Library";
    // A handler that will be notified when the task is finished
    private final Handler replyTo;
    // The user we are querying on YouTube for videos
    private final String lat;
    private final String lng;
    private final String countryCode;
    private final Handler mHandler;

    /**
     * Don't forget to call run(); to start this task
     *
     * @param replyTo - the handler you want to receive the response when this task has finished
     * @param lat     - the lat of who on YouTube you are browsing
     */
    public GetYouTubeUserVideosTask(Handler replyTo, String lat, String lng, String countryCode, Handler mHandler) {
        this.replyTo = replyTo;
        this.lat = lat;
        this.lng = lng;
        this.countryCode = countryCode;
        this.mHandler = mHandler;
    }

    @Override
    public void run() {
        try {
            // Get a httpclient to talk to the internet
            final HttpClient client = new DefaultHttpClient();
            // Perform a GET request to YouTube for a JSON list of all the videos by a specific user
//            final HttpUriRequest request = new HttpGet("https://gdata.youtube.com/feeds/api/videos?author=" + username + "&v=2&alt=jsonc");
            final HttpUriRequest request = new HttpGet("https://www.googleapis.com/youtube/v3/videos?chart=mostpopular&regionCode=" + countryCode + "&location=" + lat + "," + lng + "&locationRadius=50km&videoDefinition=high&key=AIzaSyCNTulSWUQGwhpAszY3eEzLD642ocITLTw&maxResults=20&part=snippet");
            final HttpResponse[] response = {null};
            // Get the response that YouTube sends back
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        response[0] = client.execute(request);

                        // Convert this response into a readable string
                        String jsonString = StreamUtils.convertToString(response[0].getEntity().getContent());
                        // Create a JSON object that we can use from the String
                        JSONObject json = new JSONObject(jsonString);

                        // For further information about the syntax of this request and JSON-C
                        // see the documentation on YouTube http://code.google.com/apis/youtube/2.0/developers_guide_jsonc.html

                        // Get are search result items
//                        JSONArray jsonArray = json.getJSONObject("data").getJSONArray("items");
                        JSONArray jsonArray = json.getJSONArray("items");
                        Log.d(TAG, "Youtube: " + jsonArray);

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
                        // Create a library to hold our videos
                        Library lib = new Library(lat, videos);

//                        Log.i(TAG, "Lib Object Line 118: " + lib.getVideos());

                        // Pack the Library into the bundle to send back to the Activity
                        Bundle data = new Bundle();
                        data.putSerializable(LIBRARY, lib);

                        // Send the Bundle of data (our Library) back to the handler (our Activity)
                        Message msg = Message.obtain();

                        if (data == null) {
                            Log.v(TAG, "DATA is null");
                        }
                        if (msg == null) {
                            Log.v(TAG, "MSG is null");
                        } else {
                            msg = mHandler.obtainMessage();
                        }
                        if (replyTo == null) {
                            Log.v(TAG, "MSG is null");
                        }
                        msg.setData(data);
                        Log.v(TAG, msg.getData().toString());
                        if (msg != null)
                            replyTo.sendMessage(msg);
                    } catch (Exception e) {

                        Log.i(TAG, "Exception: " + e.getMessage() + "\n\n" + e.getLocalizedMessage());
                    }
                }
            }).start();

        } catch (Exception e) {

        }
    }
}