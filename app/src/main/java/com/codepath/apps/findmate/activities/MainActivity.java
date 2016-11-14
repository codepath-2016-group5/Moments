package com.codepath.apps.findmate.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.codepath.apps.findmate.fragments.AddEventFragment;
import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.databinding.ActivityMainBinding;
import com.codepath.apps.findmate.fragments.EventsFragment;
import com.codepath.apps.findmate.fragments.ProfileFragment;
import com.codepath.apps.findmate.fragments.SettingsFragment;
import com.codepath.apps.findmate.models.Event;

public class MainActivity extends AppCompatActivity  implements AddEventFragment.OnEventCreateListener{

    ActivityMainBinding binding;
    DrawerLayout mDrawer;
    NavigationView nvDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setContentView(binding.getRoot());
        setSupportActionBar(binding.tbInclude.toolbar);
        mDrawer = binding.drawerLayout;

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(setupDrawerToggle());

        nvDrawer = binding.nvView;

        // Setup drawer view
        setupDrawerContent(nvDrawer);

        nvDrawer.getMenu().getItem(0).setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new EventsFragment()).commit();
        setTitle(R.string.events_fragment);



    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, binding.tbInclude.toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {

        // Create a new fragment and specify the fragment to show based on nav item clicked

        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_events_fragment:
                fragmentClass = EventsFragment.class;
                break;
            case R.id.nav_profile_fragment:
                fragmentClass = ProfileFragment.class;
                break;
            case R.id.nav_settings_fragment:
                fragmentClass = SettingsFragment.class;
                break;
             default:
                 fragmentClass = EventsFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
      // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();

    }

    @Override
    public void onCreate(Event event) {
        //TODO:is it right way ?
        for(Fragment f : getSupportFragmentManager().getFragments()){
            if(f instanceof  EventsFragment){
               ((EventsFragment) f).onCreateEvent(event);
            }
        }
    }
}
