package com.codepath.apps.findmate.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.codepath.apps.findmate.models.ParseUsers;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.parse.ParseUser;

/**
 * Make sure you have included the Android Maps Utility library
 * See: https://developers.google.com/maps/documentation/android-api/utility/
 *
 * Gradle config:
 *
 * dependencies {
 *   'com.google.maps.android:android-maps-utils:0.4+'
 * }
 */

public class MapUtils {

    private static BitmapDescriptor createBubble(Context context, int style, String title) {
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setStyle(style);
        Bitmap bitmap = iconGenerator.makeIcon(title);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private static Marker addMarker(GoogleMap map, LatLng point, String title,
                                   String snippet,
                                   BitmapDescriptor marker) {
        // Creates and adds marker to the map
        MarkerOptions options = new MarkerOptions()
                .position(point)
                .title(title)
//                .snippet(snippet)
                .icon(marker);
        return map.addMarker(options);
    }

    public static void addMarker(Context context, GoogleMap map, ParseUser user) {
        String name = ParseUsers.getName(user);
        // Styles are ints in {3, ..., 7} e.g. IconGenerator#StyleGreen
        int style = name.hashCode() % 5 + 3;
        BitmapDescriptor icon = MapUtils.createBubble(context, style, name);
        LatLng latLng = new LatLng(ParseUsers.getLocation(user).getLatitude(),
                ParseUsers.getLocation(user).getLongitude());
        MapUtils.addMarker(map, latLng, name, name, icon);
    }
}
