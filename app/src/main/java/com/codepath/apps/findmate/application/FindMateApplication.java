package com.codepath.apps.findmate.application;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.codepath.apps.findmate.models.Event;
import com.codepath.apps.findmate.models.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

public class FindMateApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();

        //Register parse models
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Event.class);
        ApplicationInfo app = null;
        try {
            app = getApplicationContext()
                            .getPackageManager()
                            .getApplicationInfo(getApplicationContext().getPackageName()
                                    , PackageManager.GET_META_DATA);
        }catch (PackageManager.NameNotFoundException e){

        }

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("MATEFIND") // should correspond to APP_ID env variable
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("https://findmate.herokuapp.com/parse/").build());

    }

}