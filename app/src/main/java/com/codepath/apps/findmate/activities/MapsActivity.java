package com.codepath.apps.findmate.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.client.FacebookClient;
import com.codepath.apps.findmate.databinding.ActivityMapsBinding;
import com.codepath.apps.findmate.models.Group;
import com.codepath.apps.findmate.models.User;
import com.codepath.apps.findmate.utils.MapUtils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String USER_ID_EXTRA = "userId";
    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private String userId;
    private User user;
    private List<Group> groups;
    private int selectedGroupIndex;

    private SwitchCompat switchLocation;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private NavigationView nvView;
    private DrawerLayout drawerLayout;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    //binding object
    private ActivityMapsBinding binding;

    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();
    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.d("Handlers", "Called on main thread");

            mapGroup(map);

            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(runnableCode, 2000);
        }
    };

    private FacebookClient fbClient = new FacebookClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        setContentView(binding.getRoot());

        userId = getIntent().getStringExtra(USER_ID_EXTRA);
        user = ParseObject.createWithoutData(User.class, userId);

        final FindCallback<Group> findCallback = new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                MapsActivity.this.groups = groups;
                initializeView();

                if (mapFragment != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap map) {
                            loadMap(map);
                        }
                    });
                } else {
                    Toast.makeText(MapsActivity.this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));

        // fetch user and user's groups
        if (user.isDataAvailable()) {
            fetchGroups(findCallback);
        } else {
            user.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    fetchGroups(findCallback);
                }
            });
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.maps_toolbar);
        setSupportActionBar(toolbar);

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }
    }

    private void initializeView() {
        //setup on onclick event for invite friends


        drawerLayout = binding.drawerLayout;
        drawerLayout.addDrawerListener(new DrawerListener());
        nvView = binding.nvView;
        nvView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        for (int index = 0; index < groups.size(); ++index) {
            Group group = groups.get(index);
            if (group.getName().equals(menuItem.getTitle().toString())) {
                selectedGroupIndex = index;

                menuItem.setChecked(true);
                setTitle(menuItem.getTitle());

                drawerLayout.closeDrawers();
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        MenuItem miLocation = menu.findItem(R.id.miLocation);
        //switchLocation = (SwitchCompat) miLocation.getActionView().findViewById(R.id.switchLocation);

        return super.onCreateOptionsMenu(menu);
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
//           Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            MapsActivityPermissionsDispatcher.getMyLocationWithCheck(this);
//            map.setOnMapLongClickListener(this);
//            map.setOnMarkerDragListener(this);

            handler.post(runnableCode);
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    /*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                }

        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    /*
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
           // Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    public void onLocationChanged(final Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        user.fetchIfNeededInBackground(new GetCallback<User>() {
            @Override
            public void done(User object, ParseException e) {
                publishLocation(location);
            }
        });
    }

    private void publishLocation(Location location) {
        // do not publish location when switch is disabled

        user.setLocation(new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
        user.saveInBackground();
    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        MapsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressWarnings("all")
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        if (map != null) {
            // Now that map has loaded, let's get our location!
            map.setMyLocationEnabled(true);
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            connectClient();
        }
    }

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        connectClient();
    }

    /*
	 * Called when the Activity is no longer visible.
	 */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    //define onClickListener for invite members
    private final View.OnClickListener onAppInviteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //open facebook app invite dialog.
            if (AppInviteDialog.canShow()) {
                AppInviteContent content = new AppInviteContent.Builder()
                        .setApplinkUrl(getString(R.string.fb_app_link))
                        .setPreviewImageUrl(getString(R.string.fb_image_preview_url))
                        .build();
                AppInviteDialog.show(MapsActivity.this, content);
            }
        }
    };


    private class DrawerListener implements DrawerLayout.DrawerListener {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {
            Menu menu = nvView.getMenu();
            menu.clear();

            for (Group group : groups) {
                menu.add(group.getName());
            }
            menu.getItem(selectedGroupIndex).setChecked(true);
        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    }

    private void fetchGroups(FindCallback<Group> callback) {
        fetchUserIfNeeded();
        ParseQuery.getQuery(Group.class)
                .whereEqualTo(Group.MEMBERS_KEY, user)
                .findInBackground(callback);
    }

    private void fetchGroupsByInviteCode(String inviteCode, FindCallback<Group> callback) {
        fetchUserIfNeeded();
        ParseQuery.getQuery(Group.class)
                .whereEqualTo(Group.INVITE_KEY, inviteCode)
                .findInBackground(callback);
    }

    private void fetchUserIfNeeded() {
        // FIXME : avoid blocking
        try {
            user.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Group getSelectedGroup() {
        return groups.get(selectedGroupIndex);
    }

    private void mapGroup(final GoogleMap map) {
        if (groups.size() == 0) {
            return;
        }

        ParseQuery.getQuery(Group.class)
                .include(Group.MEMBERS_KEY)
                .getInBackground(getSelectedGroup().getObjectId(), new GetCallback<Group>() {
                    @Override
                    public void done(Group group, ParseException e) {
                        map.clear();
                        for (User member : group.getMembers()) {
                            if(member.getLocation() != null) {
                                addProfilePicMarker(member);
                            }
                        }
                    }
                });
    }

    private void addProfilePicMarker(final User member) {
        String userId = member.getFbId();

        if(member.getProfilePic() != null) {
            MapUtils.addProfilePicMarker(MapsActivity.this, member, map);
        } else {

            fbClient.getProfilePic(userId,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {
                                String profilePicURL = (String) response.getJSONObject().getJSONObject("data").get("url");

                                member.setProfilePic(profilePicURL);
                                member.saveInBackground();

                                MapUtils.addProfilePicMarker(MapsActivity.this, member, map);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        }
    }

    public void onCreateGroupClick(MenuItem item) {
        drawerLayout.closeDrawers();

        new MaterialDialog.Builder(MapsActivity.this)
                .title(R.string.create_group)
                .input(R.string.group_name, R.string.empty, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                    }
                })
                .positiveText("Create")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        final Group group = new Group()
                                .setInviteCode()
                                .setName(dialog.getInputEditText().getText().toString())
                                .addMember(user);
                        group.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                groups.add(group);
                                // FIXME : need to rerender
                                selectedGroupIndex = groups.indexOf(group);
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void onJoinGroupClick(MenuItem item) {
        drawerLayout.closeDrawers();

        new MaterialDialog.Builder(MapsActivity.this)
                .title(R.string.join_group)
                .input(R.string.invite_code, R.string.empty, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                    }
                })
                .positiveText("Join")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        String inviteCode = dialog.getInputEditText().getText().toString();
                        Group.getGroupByInviteCode(inviteCode, new FindCallback<Group>() {
                            @Override
                            public void done(List<Group> objects, ParseException e) {
                                if (objects.isEmpty()) {
                                    dialog.dismiss();
                                    Toast.makeText(MapsActivity.this, "Could not find group",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    final Group group = objects.get(0);
                                    group.addMember(user);
                                    group.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            groups.add(group);
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void onInviteToAppClick(MenuItem item) {
        drawerLayout.closeDrawers();

        // open facebook app invite dialog.
        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(getString(R.string.fb_app_link))
                    .setPreviewImageUrl(getString(R.string.fb_image_preview_url))
                    .build();
            AppInviteDialog.show(MapsActivity.this, content);
        }
    }


}
