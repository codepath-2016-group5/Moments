package com.codepath.apps.findmate.client;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FacebookApi;

/**
 * Created by mdathrika on 11/12/16.
 */

public class FacebookClient extends OAuthBaseClient {

    public static final Class<? extends Api> REST_API_CLASS = FacebookApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "cXmhMjYDJikV7Yro56R7wC4M4";       // Change this
    public static final String REST_CONSUMER_SECRET = "LWYg15E0G45Cjxf7YbocjHat5S5t78o2cpvnWwH2o8DjzGjsOx"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

    public FacebookClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }
}
