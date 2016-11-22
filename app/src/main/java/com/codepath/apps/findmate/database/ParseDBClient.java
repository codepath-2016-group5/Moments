package com.codepath.apps.findmate.database;

import com.codepath.apps.findmate.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdathrika on 11/19/16.
 */

public class ParseDBClient {

    public void saveCurrentLocation(String userId, final Double lat, final Double lon) {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.getInBackground(userId, new GetCallback<User>() {
            public void done(User user, ParseException e) {
                if (e == null) {
                    user.setLocation(new ParseGeoPoint(lat, lon));
                    user.saveInBackground();
                }
            }
        });
    }

    public void getFriendsLocation(Collection<String> fbIds, FindCallback<User> callback) {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereContainedIn("fb_id", fbIds);
        query.findInBackground(callback);

    }
}
