package com.codepath.apps.findmate.activities;

import com.parse.ui.ParseLoginDispatchActivity;

public class DispatchActivity extends ParseLoginDispatchActivity {
    @Override
    protected Class<?> getTargetClass() {
        return MapsActivity.class;
    }
}
