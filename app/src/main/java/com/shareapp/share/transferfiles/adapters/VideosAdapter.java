package com.shareapp.share.transferfiles.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.shareapp.share.transferfiles.R;
import com.shareapp.share.transferfiles.domain.Video;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * This adapter is used to show our Video objects in a ListView
 * It hasn't got many memory optimisations, if your list is getting bigger or more complex
 * you may want to look at better using your view resources: http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/view/List14.html
 *
 * @author paul.blundell
 */
public class VideosAdapter extends BaseAdapter {
    // The list of videos to display
    List<Video> videos;
    // An inflator to use when creating rows
    private LayoutInflater mInflater;
    private Context context;
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;



    /**
     * @param context this is the context that the list will be shown in - used to create new list rows
     * @param videos  this is a list of videos to display
     */
    public VideosAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // If convertView wasn't null it means we have already set it to our list_item_user_video so no need to do it again
        if (convertView == null) {
            // This is the layout we are using for each row in our list
            // anything you declare in this layout can then be referenced below
            convertView = mInflater.inflate(R.layout.list_item_user_video, null);
        }
        // We are using a custom imageview so that we can load images using urls
        // For further explanation see: http://blog.blundell-apps.com/imageview-with-loading-spinner/
//		UrlImageView thumb = (UrlImageView) convertView.findViewById(R.id.userVideoThumbImageView);
//		ImageView ivThumb = (ImageView) convertView.findViewById(R.id.userVideoThumbImageView);


        // Get a single video from our list
        final Video video = videos.get(position);

        youTubeView = (YouTubePlayerView) convertView.findViewById(R.id.youtube_view);
        youTubeView.initialize("AIzaSyCNTulSWUQGwhpAszY3eEzLD642ocITLTw", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (!b)
                    youTubePlayer.cueVideo(video.getUrl());
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                if (youTubeInitializationResult.isUserRecoverableError()) {
//                    youTubeInitializationResult.getErrorDialog(, RECOVERY_REQUEST).show();
                } else {
//                    String error = String.format(context.getString(R.string.player_error), youTubeInitializationResult.toString());
                    Toast.makeText(context, "Video not displayed", Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView title = (TextView) convertView.findViewById(R.id.userVideoTitleTextView);
        // Set the image for the list item
//		thumb.setImageDrawable(video.getThumbUrl());
//        ivThumb.setImageDrawable(video.getThumbUrl());

        // Set the title for the list item
        title.setText(video.getTitle());

        return convertView;
    }


    public class youtube extends YouTubeBaseActivity{

    }
}