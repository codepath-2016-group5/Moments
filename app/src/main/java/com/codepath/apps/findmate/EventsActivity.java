package com.codepath.apps.findmate;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.codepath.apps.findmate.models.Event;

public class EventsActivity extends AppCompatActivity
        implements AddEventFragment.OnEventCreateListener {

    private EventsFragment eventsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        eventsFragment = (EventsFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragmentEvents);
    }

    public void onClick(View view) {
        showAddEventFragment();
    }

    private void showAddEventFragment() {
        FragmentManager fm = getSupportFragmentManager();
        AddEventFragment addEventFragment = AddEventFragment.newInstance();
        addEventFragment.show(fm, "fragment_add_event");
    }

    @Override
    public void onCreate(Event event) {
        eventsFragment.onCreateEvent(event);
    }
}
