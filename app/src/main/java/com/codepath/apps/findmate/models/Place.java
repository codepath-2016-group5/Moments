package com.codepath.apps.findmate.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Place")
public class Place extends ParseObject {

    public static final String ID_KEY = "id";
    public static final String LOCATION_KEY = "location";
    public static final String NAME_KEY = "name";
    public static final String ADDRESS_KEY = "address";

    public static Place create(com.google.android.gms.location.places.Place place) {
        Place parsePlace = new Place();
        parsePlace.setId(place.getId())
                .setName(place.getName().toString())
                .setAddress(place.getAddress().toString())
                .setLocation(new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude));
        return parsePlace;
    }

    public Place() {
    }

    public String getId() {
        return getString(ID_KEY);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(LOCATION_KEY);
    }

    public String getName() {
        return getString(NAME_KEY);
    }

    public String getAddress() {
        return getString(ADDRESS_KEY);
    }

    public Place setId(String id) {
        put(ID_KEY, id);
        return this;
    }

    public Place setLocation(ParseGeoPoint location) {
        put(LOCATION_KEY, location);
        return this;
    }

    public Place setName(String name) {
        put(NAME_KEY, name);
        return this;
    }

    public Place setAddress(String address) {
        put(ADDRESS_KEY, address);
        return this;
    }
}
