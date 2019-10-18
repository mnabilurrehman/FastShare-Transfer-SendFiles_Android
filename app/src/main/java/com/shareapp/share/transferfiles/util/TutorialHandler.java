package com.shareapp.share.transferfiles.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by NabilurRehman on 10/27/2017.
 */

public class TutorialHandler {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;


    private String FILE_NAME = "tutorial";
    private String KEY_IS_TUTORIAL_SEEN = "isTutorialSeen";
    private int MODE = 0;

    public TutorialHandler(Context context){
        this.context = context;
        preferences =  context.getSharedPreferences(FILE_NAME, MODE);
        editor = preferences.edit();
    }

    public void setTutorialState(Boolean isTutorialSeen){
        editor.putBoolean(KEY_IS_TUTORIAL_SEEN, isTutorialSeen);
        editor.commit();
    }

    public Boolean getTutorialState(){
        Boolean isTutorialSeen = preferences.getBoolean(KEY_IS_TUTORIAL_SEEN, false);
        return isTutorialSeen;
    }

}
