package com.codepath.apps.findmate.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.models.CheckIn;
import com.codepath.apps.findmate.models.ParseUsers;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.parse.ParseGeoPoint;
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

    private static BitmapDescriptor createUserBubble(Context context, String title) {
        IconGenerator iconGenerator = new IconGenerator(context);

        Drawable circle = context.getResources().getDrawable(R.drawable.ic_circle);
        circle.setTint(DrawableUtils.getColor(title));
        iconGenerator.setBackground(circle);
        iconGenerator.setTextAppearance(R.style.MarkerText); // this forces text color to white

        Bitmap bitmap = iconGenerator.makeIcon(title.toUpperCase().substring(0, 1));
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private static BitmapDescriptor createCheckIn(Context context, String name) {
        IconGenerator iconGenerator = new IconGenerator(context);

        Drawable bubble = context.getResources().getDrawable(R.drawable.ic_bubble);
        bubble.setTint(DrawableUtils.getColor(name));
        iconGenerator.setBackground(bubble);

        Bitmap bitmap = iconGenerator.makeIcon();
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private static Marker addMarker(GoogleMap map, LatLng point, String title,
            String snippet, BitmapDescriptor marker) {
        MarkerOptions options = new MarkerOptions()
                .position(point)
                .title(title)
                .snippet(snippet)
                .icon(marker);
        return map.addMarker(options);
    }

    public static void addUserMarker(Context context, GoogleMap map, ParseUser user) {
        String name = ParseUsers.getName(user);
        BitmapDescriptor icon = MapUtils.createUserBubble(context, name);
        LatLng latLng = new LatLng(ParseUsers.getLocation(user).getLatitude(),
                ParseUsers.getLocation(user).getLongitude());
        MapUtils.addMarker(map, latLng, name, name, icon);
    }

    public static void addCheckInMarker(Context context, GoogleMap map, CheckIn checkIn) {
        String name = ParseUsers.getName(checkIn.getCreator());
        BitmapDescriptor icon = MapUtils.createCheckIn(context, name);
        ParseGeoPoint location = checkIn.getPlace().getLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MapUtils.addMarker(map, latLng, checkIn.getDescription(), checkIn.getDescription(), icon);
    }
}
