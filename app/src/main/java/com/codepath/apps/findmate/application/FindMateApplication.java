package com.codepath.apps.findmate.application;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.codepath.apps.findmate.models.CheckIn;
import com.codepath.apps.findmate.models.Group;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

public class FindMateApplication extends Application {

    public static final int FACEBOOK_REQUEST_CODE = 20;

    @Override
    public void onCreate() {

        super.onCreate();

        // Register parse models
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(CheckIn.class);

        ApplicationInfo app = null;
        try {
            app = getApplicationContext()
                            .getPackageManager()
                            .getApplicationInfo(getApplicationContext().getPackageName()
                                    , PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e){

        }

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("MATEFIND") // should correspond to APP_ID env variable
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("https://findmate.herokuapp.com/parse/").build());
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        ParseFacebookUtils.initialize(this, FACEBOOK_REQUEST_CODE);
    }
}
