package com.codepath.apps.findmate.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.apps.findmate.fragments.MapsFragment;
import com.codepath.apps.findmate.fragments.TimelineFragment;
import com.codepath.apps.findmate.interfaces.ViewPagerFragment;
import com.codepath.apps.findmate.models.CheckIn;
import com.codepath.apps.findmate.models.Group;
import com.codepath.apps.findmate.models.ParseUsers;
import com.codepath.apps.findmate.models.Place;
import com.codepath.apps.findmate.utils.DrawableUtils;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginBuilder;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapsActivity";

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int CHECK_IN_REQUEST_CODE = 2;

    private ParseUser user;
    private List<Group> groups;
    // FIXME : need to persist selected group per user; persist sharingEnabled per user-group
    @Nullable
    private Integer selectedGroupIndex;
    private boolean sharingEnabled = true;

    // the place selected by the user
    private Place place;

    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvView;
    private DrawerLayout drawerLayout;
    private SmartFragmentStatePagerAdapter adapterViewPager;
    private ViewPager vpPager;

    // Create the Handler object (on the main thread by default)
    private Handler handler = new Handler();
    // Define the code block to be executed
    private Runnable refreshGroup = new Runnable() {
        @Override
        public void run() {
            Log.i("Handler", "Refresh group called on the main thread");

            // Do nothing if the user has no selected group
            if (getSelectedGroup() == null) {
                // Repeat this the same runnable code block again another 2 seconds
                handler.postDelayed(refreshGroup, 2000);
                return;
            } else {
                Group.getGroupById(getSelectedGroup().getObjectId(), new GetCallback<Group>() {
                    @Override
                    public void done(Group group, ParseException e) {
                        onGroupUpdated();

                        // Repeat this the same runnable code block again another 2 seconds
                        handler.postDelayed(refreshGroup, 2000);
                    }
                });
            }
        }
    };

    @Nullable
    private Group getSelectedGroup() {
        if (selectedGroupIndex == null) {
            // select the first group by default
            if (groups.isEmpty()) {
                return null;
            } else {
                selectedGroupIndex = 0;
                return groups.get(selectedGroupIndex);
            }
        } else {
            if (groups == null) {
                groups = new ArrayList<>();
            }
            return groups.get(selectedGroupIndex);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        user = ParseUser.getCurrentUser();
        groups = new ArrayList<>();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        nvView = (NavigationView) findViewById(R.id.nvView);
        nvView.setNavigationItemSelectedListener(this);
        setupNavHeader();

        Toolbar toolbar = (Toolbar) findViewById(R.id.maps_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = setupDrawerToggle(toolbar);
        drawerLayout.addDrawerListener(drawerToggle);

        vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new PagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_map);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home);

        Group.getGroupsByUser(user, new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                MapsActivity.this.groups = groups;
                MapsActivity.this.selectedGroupIndex = 0;

                onGroupUpdated();
            }
        });
    }

    private void setupNavHeader() {
        View headerView = nvView.getHeaderView(0);
        ImageView ivNavProfile = (ImageView) headerView.findViewById(R.id.ivNavProfile);
        TextView tvNavName = (TextView) headerView.findViewById(R.id.tvNavName);
        TextView tvNavEmail = (TextView) headerView.findViewById(R.id.tvNavEmail);

        ivNavProfile.setImageDrawable(DrawableUtils.getInitialsDrawable(user));
        tvNavName.setText(ParseUsers.getName(user));
        tvNavEmail.setText(user.getEmail());
    }

    private ActionBarDrawerToggle setupDrawerToggle(Toolbar toolbar) {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close);
    }

    private void onGroupUpdated() {
        // Update the title with the selected group name
        if (getSelectedGroup() != null) {
            setTitle(getSelectedGroup().getName());
        }

        // Setup the menu
        initMenu(nvView.getMenu());

        ViewPagerFragment fragment = (ViewPagerFragment) adapterViewPager
                .getRegisteredFragment(vpPager.getCurrentItem());
        fragment.onGroupUpdated(getSelectedGroup());
    }

    private void initMenu(Menu menu) {
        MenuItem miGroups = menu.findItem(R.id.nav_groups_item);
        SubMenu groupsSubMenu = miGroups.getSubMenu();
        groupsSubMenu.setGroupVisible(R.id.nav_groups_group, true);

        groupsSubMenu.clear();
        for (Group group : groups) {
            MenuItem miGroup = groupsSubMenu.add(group.getName());

            if (getSelectedGroup() != null &&
                    group.getObjectId().equals(getSelectedGroup().getObjectId())) {
                miGroup.setChecked(true);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.post(refreshGroup);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(refreshGroup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerToggle.onOptionsItemSelected(item);
                return true;

            case R.id.miInviteGroup:
                showInviteToGroupDialog();
                return true;

            case R.id.miLocation:
                showLocationSharingDialog();
                return true;

            case R.id.miLogout:
                ParseUser.logOut();

                // start login activity
                Intent intent = new ParseLoginBuilder(MapsActivity.this).build();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        for (int index = 0; index < groups.size(); ++index) {
            Group group = groups.get(index);
            if (group.getName().equals(item.getTitle().toString())) {
                selectedGroupIndex = index;

                onGroupUpdated();

                drawerLayout.closeDrawers();
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    place = Place.create(PlaceAutocomplete.getPlace(this, data));
                    Log.i(TAG, "Place: " + place.getName());

                    // Launch checkin activity to continue the flow
                    Intent intent = new Intent(MapsActivity.this, CheckInActivity.class);
                    intent.putExtra("ADDRESS", place.getAddress());
                    intent.putExtra("LAT", place.getLocation().getLatitude());
                    intent.putExtra("LONG", place.getLocation().getLongitude());
                    startActivityForResult(intent, CHECK_IN_REQUEST_CODE);
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    Log.e(TAG, status.getStatusMessage());
                    Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_LONG).show();
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                    // Do nothing
                }

                break;
            }
            case CHECK_IN_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    String description = data.getStringExtra(CheckInActivity.DESCRIPTION_EXTRA);
                    final CheckIn checkIn = new CheckIn()
                            .setCreator(user)
                            .setDescription(description);
                    if (place != null) {
                        checkIn.setPlace(place);
                    }

                    // Workaround for RuntimeException: only ACLs can be stored in the ACL key
                    // https://github.com/ParsePlatform/Parse-SDK-Android/issues/499
                    checkIn.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            getSelectedGroup().addCheckIn(checkIn);
                            getSelectedGroup().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                }
                            });
                        }
                    });
                } else if (resultCode == RESULT_CANCELED) {
                    place = null;
                }
                break;
            }
            default:
                break;
        }
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void onCheckInClick(View view) {
        try {
            PlaceAutocomplete.IntentBuilder builder = new PlaceAutocomplete.IntentBuilder(
                    PlaceAutocomplete.MODE_FULLSCREEN);

            // bias for locations near user's current location
            ParseGeoPoint location = ParseUsers.getLocation(user);
            if (location != null) {
                builder.setBoundsBias(LatLngBounds.builder()
                        .include(new LatLng(location.getLatitude(), location.getLongitude()))
                        .build());
            }

            Intent intent = builder.build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
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
                                selectedGroupIndex = groups.indexOf(group);

                                // Re-render with the newly created group
                                onGroupUpdated();

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
                                            selectedGroupIndex = groups.indexOf(group);

                                            // Re-render with the newly joined group
                                            onGroupUpdated();

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

    private void showLocationSharingDialog() {
        new MaterialDialog.Builder(MapsActivity.this)
                .title(R.string.location_sharing)
                .items(R.array.location_sharing_items)
                .itemsCallbackSingleChoice(sharingEnabled ? 0 : 1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which,
                            CharSequence text) {
                        return true;
                    }
                })
                .positiveText("Save")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        sharingEnabled = dialog.getSelectedIndex() == 0;
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

    private void showInviteToGroupDialog() {
        new MaterialDialog.Builder(MapsActivity.this)
                .title(R.string.invite_code)
                .content(getSelectedGroup().getInviteCode())
                .positiveText("Done")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class PagerAdapter extends SmartFragmentStatePagerAdapter {

        private int NUM_ITEMS = 2;

        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that pageØ
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MapsFragment.newInstance();
                case 1:
                    return TimelineFragment.newInstance();
                default:
                    return null;
            }
        }
    }
}
