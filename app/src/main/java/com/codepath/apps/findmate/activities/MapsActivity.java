package com.codepath.apps.findmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.client.FacebookClient;
import com.codepath.apps.findmate.fragments.MapsFragment;
import com.codepath.apps.findmate.models.Group;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginBuilder;

import java.util.List;

public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MapsActivity";

    private ParseUser user;
    private List<Group> groups;
    // FIXME : need to persist selected group per user; persist sharingEnabled per user-group
    @Nullable
    private Integer selectedGroupIndex;
    private boolean sharingEnabled = true;

    private NavigationView nvView;
    private DrawerLayout drawerLayout;
    private MapsFragment mapsFragment;

    private FacebookClient fbClient = new FacebookClient();

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
            return groups.get(selectedGroupIndex);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        user = ParseUser.getCurrentUser();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        nvView = (NavigationView) findViewById(R.id.nvView);
        nvView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.maps_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Group.getGroupsByUser(user, new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                MapsActivity.this.groups = groups;
                MapsActivity.this.selectedGroupIndex = 0;

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                mapsFragment = MapsFragment.newInstance(
                        groups.get(selectedGroupIndex).getObjectId());
                ft.replace(R.id.flMaps, mapsFragment);
                ft.commit();

                onGroupUpdated();
            }
        });
    }

    private void onGroupUpdated() {
        // Update the title with the selected group name
        if (getSelectedGroup() != null) {
            setTitle(getSelectedGroup().getName());
        }

        // Setup the menu
        initMenu(nvView.getMenu());

        mapsFragment.updateGroup(getSelectedGroup());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
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
}
