package com.codepath.apps.findmate.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.io.Serializable;
import java.util.List;

@ParseClassName("User")
public class User extends ParseObject implements Serializable {

    private static final String NAME_KEY = "name";
    private static final String EMAIL = "email";
    private static final String FULL_NAME = "full_name";
    private static final String FB_ID = "fb_id";
    private static final String FRIENDS ="friends";
    private static final String LOCATION = "location";

    public User() {
    }

    public String getName() {
        return getString(NAME_KEY);
    }

    public void setName(String name) {
        put(NAME_KEY, name);
    }

    public String getEmail() {
        return getString(EMAIL);
    }

    public void setEmail(String email) {
        put(EMAIL, email);
    }

    public String getFullName() {
        return getString(FULL_NAME);
    }

    public void setFullName(String fullName) {
        put(FULL_NAME, fullName);
    }

    public String getFbId() {
        return getString(FB_ID);
    }

    public void setFbId(String fbId) {
        put(FB_ID, fbId);
    }

    public List<User> getFriends() {
        return getList(FRIENDS);
    }

    public void setFriends(List<User> friends) {
        put(FRIENDS, friends);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(LOCATION);
    }

    public void setLocation(ParseGeoPoint location) {
        put(LOCATION, location);
    }
}
