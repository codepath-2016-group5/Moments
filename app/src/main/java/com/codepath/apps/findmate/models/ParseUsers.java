package com.codepath.apps.findmate.models;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

/**
 * Define static methods to set and retrieve location rather than subclass ParseUser
 * as a workaround for:
 *
 * E/AndroidRuntime: FATAL EXCEPTION: main
 * Process: com.codepath.apps.findmate, PID: 15432
 * java.lang.IllegalArgumentException: You must create this type of ParseObject using ParseObject.create() or the proper subclass.
 * at com.parse.ParseObject.<init>(ParseObject.java:365)
 * at com.parse.ParseObject.<init>(ParseObject.java:334)
 * at com.parse.ParseUser.<init>(ParseUser.java:162)
 * at com.parse.ui.ParseSignupFragment.onClick(ParseSignupFragment.java:178)
 *
 * The signup fragment in the Parse UI library calls new ParseUser() instead of the subclass.
 */
public class ParseUsers {

    private static final String LOCATION = "location";

    public static ParseGeoPoint getLocation(ParseUser user) {
        return user.getParseGeoPoint(LOCATION);
    }

    public static void setLocation(ParseUser user, ParseGeoPoint location) {
        user.put(LOCATION, location);
    }
}
