package com.codepath.apps.findmate.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("CheckIn")
public class CheckIn extends ParseObject {

    public static final String PLACE_KEY = "place";
    public static final String DESCRIPTION_KEY = "description";

    public CheckIn() {
    }

    public ParseObject getPlace() {
        return getParseObject(PLACE_KEY);
    }

    public String getDescription() {
        return getString(DESCRIPTION_KEY);
    }

    public CheckIn setPlace(ParseObject place) {
        put(PLACE_KEY, place);
        return this;
    }

    public CheckIn setDescription(String description) {
        put(DESCRIPTION_KEY, description);
        return this;
    }
}
