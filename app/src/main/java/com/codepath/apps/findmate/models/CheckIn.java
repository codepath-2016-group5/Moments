package com.codepath.apps.findmate.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("CheckIn")
public class CheckIn extends ParseObject {

    public static final String LOCATION_KEY = "location";
    public static final String DESCRIPTION_KEY = "description";

    public CheckIn() {
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(LOCATION_KEY);
    }

    public String getDescription() {
        return getString(DESCRIPTION_KEY);
    }

    public CheckIn setLocation(ParseGeoPoint location) {
        put(LOCATION_KEY, location);
        return this;
    }

    public CheckIn setDescription(String description) {
        put(DESCRIPTION_KEY, description);
        return this;
    }
}
