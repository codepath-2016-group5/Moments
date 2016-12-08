package com.codepath.apps.findmate.client;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;

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
