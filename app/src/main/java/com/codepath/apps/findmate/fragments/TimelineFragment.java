package com.codepath.apps.findmate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.interfaces.ViewPagerFragment;
import com.codepath.apps.findmate.models.Group;

public class TimelineFragment extends Fragment implements ViewPagerFragment {

    public static TimelineFragment newInstance() {
        return new TimelineFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onGroupUpdated(Group group) {
    }
}
