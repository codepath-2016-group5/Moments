package com.codepath.apps.findmate.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.models.CheckIn;
import com.codepath.apps.findmate.models.ParseUsers;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import static com.codepath.apps.findmate.models.ParseUsers.getLocation;

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
    /**
     * Marker item on the map; Either a user or a checkin.
     */
    public static class MarkerItem implements ClusterItem {

        private final LatLng position;
        @Nullable
        private ParseUser user;
        @Nullable
        private CheckIn checkIn;

        public MarkerItem(@NonNull ParseUser user) {
            ParseGeoPoint parseGeoPoint = ParseUsers.getLocation(user);
            this.position = new LatLng(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude());
            this.user = user;
        }

        public MarkerItem(@NonNull CheckIn checkIn) {
            ParseGeoPoint parseGeoPoint = checkIn.getPlace().getLocation();
            this.position = new LatLng(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude());
            this.checkIn = checkIn;
        }

        @Override
        public LatLng getPosition() {
            return position;
        }
    }

    /**
     * Draws marker items. When there are multiple item in the cluster, draw a number.
     */
    public static class MarkerItemRenderer extends DefaultClusterRenderer<MarkerItem> {

        private final Context context;

        public MarkerItemRenderer(Context context, GoogleMap map, ClusterManager clusterManager) {
            super(context, map, clusterManager);
            this.context = context;
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerItem markerItem, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            if (markerItem.user != null) {
                addUserMarker(markerItem.user, markerOptions);
            } else if (markerItem.checkIn != null) {
                addCheckInMarker(markerItem.checkIn, markerOptions);
            }
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MarkerItem> cluster, MarkerOptions markerOptions) {
            addClusterMarker(cluster, markerOptions);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }

        private BitmapDescriptor createUser(Context context, String title) {
            IconGenerator iconGenerator = new IconGenerator(context);

            Drawable circle = context.getResources().getDrawable(R.drawable.ic_circle);
            circle.setTint(DrawableUtils.getColor(title));
            iconGenerator.setBackground(circle);
            iconGenerator.setTextAppearance(R.style.MarkerText); // this forces text color to white

            Bitmap bitmap = iconGenerator.makeIcon(title.toUpperCase().substring(0, 1));
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }

        private BitmapDescriptor createCheckIn(Context context, String name) {
            IconGenerator iconGenerator = new IconGenerator(context);

            Drawable bubble = context.getResources().getDrawable(R.drawable.ic_bubble);
            bubble.setTint(DrawableUtils.getColor(name));
            iconGenerator.setBackground(bubble);

            Bitmap bitmap = iconGenerator.makeIcon();
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }

        private BitmapDescriptor createCluster(Context context, Cluster<MarkerItem> cluster) {
            IconGenerator iconGenerator = new IconGenerator(context);

            Drawable circle = context.getResources().getDrawable(R.drawable.ic_circle);
            circle.setTint(context.getResources().getColor(R.color.colorAccent));
            iconGenerator.setBackground(circle);
            iconGenerator.setTextAppearance(R.style.MarkerText); // this forces text color to white

            Bitmap bitmap = iconGenerator.makeIcon(Integer.toString(cluster.getSize()));
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }


        private void markerOptions(MarkerOptions markerOptions, LatLng point, String title,
                String snippet, BitmapDescriptor marker) {
            markerOptions
                    .position(point)
                    .icon(marker);
            if (title != null) {
                markerOptions.title(title);
            }
            if (snippet != null) {
                markerOptions.snippet(snippet);
            }
        }

        private void addClusterMarker(Cluster<MarkerItem> cluster, MarkerOptions markerOptions) {
            BitmapDescriptor icon = createCluster(context, cluster);
            markerOptions(markerOptions, cluster.getPosition(), null, null, icon);
        }

        private void addUserMarker(ParseUser user, MarkerOptions markerOptions) {
            String name = ParseUsers.getName(user);
            BitmapDescriptor icon = createUser(context, name);
            LatLng latLng = new LatLng(getLocation(user).getLatitude(),
                    getLocation(user).getLongitude());

            markerOptions(markerOptions, latLng, name, name, icon);
        }

        private void addCheckInMarker(CheckIn checkIn, MarkerOptions markerOptions) {
            String name = ParseUsers.getName(checkIn.getCreator());
            BitmapDescriptor icon = createCheckIn(context, name);
            ParseGeoPoint location = checkIn.getPlace().getLocation();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            markerOptions(markerOptions, latLng, checkIn.getDescription(), checkIn.getDescription(), icon);
        }
    }
}
