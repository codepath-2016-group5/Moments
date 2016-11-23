package com.codepath.apps.findmate.client;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;

/**
 * Created by mdathrika on 11/12/16.
 */

public class FacebookClient {

    public void getProfilePic(String userId, GraphRequest.Callback callback) {

        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        params.putString("type", "normal");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + userId+"/picture",
                params,
                HttpMethod.GET,
                callback).executeAsync();
    }
}
