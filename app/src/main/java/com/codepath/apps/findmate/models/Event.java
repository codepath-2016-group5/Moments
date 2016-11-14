package com.codepath.apps.findmate.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Event")
public class Event extends ParseObject {

    private static final String NAME_KEY = "name";
    private static final String PICTURE_URL_KEY = "picture";
    private static final String START_KEY = "start";
    private static final String END_KEY = "end";
    private static final String LOCATION_KEY = "location";
    private static final String DETAILS_KEY = "details";

    public Event() {
        super();
    }

    public String getName() {
        return getString(NAME_KEY);
    }

    public String getPictureUrl() {
        return getString(PICTURE_URL_KEY);
    }

    public Date getStart() {
        return getDate(START_KEY);
    }

    public Date getEnd() {
        return getDate(END_KEY);
    }

    public ParseGeoPoint getLocation() {
        return (ParseGeoPoint) get(LOCATION_KEY);
    }

    public String getDetails() {
        return getString(DETAILS_KEY);
    }

    public Event setName(String name) {
        put(NAME_KEY, name);
        return this;
    }

    public Event setPictureUrl(String pictureUrl) {
        put(PICTURE_URL_KEY, pictureUrl);
        return this;
    }

    public Event setStart(Date start) {
        put(START_KEY, start);
        return this;
    }

    public Event setEnd(Date end) {
        put(END_KEY, end);
        return this;
    }

    public Event setLocation(ParseGeoPoint location) {
        put(LOCATION_KEY, location);
        return this;
    }

    public Event setDetails(String details) {
        put(DETAILS_KEY, details);
        return this;
    }
}
