package com.codepath.apps.findmate.models;

import com.facebook.AccessToken;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.name;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String LOCATION = "location";

    public static User getCurrentUser() {
        return (User) ParseUser.getCurrentUser();
    }

    public User() {
        super();
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(LOCATION);
    }

    public void setLocation(ParseGeoPoint location) {
        put(LOCATION, location);
    }
}
