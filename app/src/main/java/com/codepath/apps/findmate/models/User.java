package com.codepath.apps.findmate.models;

import com.facebook.AccessToken;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.name;

@ParseClassName("User")
public class User extends ParseObject implements Serializable {

    public static final String NAME_KEY = "name";
    public static final String EMAIL = "email";
    public static final String FULL_NAME = "full_name";
    public static final String FB_ID = "fb_id";
    public static final String FRIENDS ="friends";
    public static final String LOCATION = "location";
    public static final String PROFILE_PIC = "profile_pic";

    public User() {
        super();
    }

    public static User fromJSONObject(AccessToken accessToken, JSONObject object) throws JSONException {
        User user = new User();

        String email = object.getString("email");
        String name = object.getString("name"); // 01/31/1980 format

        user.setFullName(name);
        user.setName(name);
        user.setEmail(email);
        user.setFbId(accessToken.getUserId());

        if(object.has("friends") && object.getJSONObject("friends").has("data")) {
            JSONArray friends = object.getJSONObject("friends").getJSONArray("data");
            Map<String, String> friendsList = new HashMap<String, String>();
            for(int i=0; i<friends.length(); i++) {
                try {
                    JSONObject friend =  (JSONObject)friends.get(i);
                    friendsList.put((String)friend.get("id"), (String)friend.get("name"));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            user.setFriends(friendsList);
        }
        return user;
    }

    public String getName() {
        return getString(NAME_KEY);
    }

    public void setName(String name) {
        put(NAME_KEY, name);
    }

    public String getEmail() {
        return getString(EMAIL);
    }

    public void setEmail(String email) {
        put(EMAIL, email);
    }

    public String getFullName() {
        return getString(FULL_NAME);
    }

    public void setFullName(String fullName) {
        put(FULL_NAME, fullName);
    }

    public String getFbId() {
        return getString(FB_ID);
    }

    public void setFbId(String fbId) {
        put(FB_ID, fbId);
    }

    public Map<String, String> getFriends() {
        return getMap(FRIENDS);
    }

    public void setFriends(Map<String, String> friends) {
        put(FRIENDS, friends);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(LOCATION);
    }

    public void setLocation(ParseGeoPoint location) {
        put(LOCATION, location);
    }

    public String getProfilePic() {
        return getString(PROFILE_PIC);
    }

    public void setProfilePic(String profilePic) {
        put(PROFILE_PIC, profilePic);
    }
}
