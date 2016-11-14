package com.codepath.apps.findmate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.findmate.models.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EventsFragment extends Fragment {

    private List<Event> events;
    private EventsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        events = createEventsList();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvEvents = (RecyclerView) view.findViewById(R.id.rvEvents);
        adapter = new EventsAdapter(getActivity(), events);
        rvEvents.setAdapter(adapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void onCreateEvent(Event event) {
        // add to head of events
        Collections.reverse(events);
        events.add(event);
        Collections.reverse(events);

        adapter.notifyDataSetChanged();
    }

    private static List<Event> createEventsList() {
        Event movie = new Event()
                .setName("Doctor Strange Viewing")
                .setDetails("Dr. Stephen Strange's (Benedict Cumberbatch) life changes after a car " +
                        "accident robs him of the use of his hands. When traditional medicine fails " +
                        "him, he looks for healing, and hope, in a mysterious enclave. He quickly " +
                        "learns that the enclave is at the front line of a battle against unseen " +
                        "dark forces bent on destroying reality. Before long, Strange is forced " +
                        "to choose between his life of fortune and status or leave it all behind " +
                        "to defend the world as the most powerful sorcerer in existence.")
                .setPictureUrl("https://i.annihil.us/u/prod/marvel/i/mg/b/d0/568ad0a32ead4.jpg")
                .setStart(new Date());
        Event sport = new Event()
                .setName("Tennis match")
                .setDetails("Play tennis doubles with group")
                .setStart(new Date());

        List<Event> events = new ArrayList<>();
        events.add(movie);
        events.add(sport);
        return events;
    }
}
