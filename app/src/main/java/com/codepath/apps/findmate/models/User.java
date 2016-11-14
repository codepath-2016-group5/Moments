package com.codepath.apps.findmate.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by sdevired on 11/9/16.
 */
@ParseClassName("User")
public class User extends ParseObject implements Parcelable {


    public static final String ID_KEY = "id";

    public static final String NAME_KEY = "name";

    public static final String EMAIL = "email";

    public static final String FULL_NAME = "full_name";

    public static final String FB_ID = "fb_id";

    public static final String FRIENDS ="friends";


    public String getId() {

        return getString(ID_KEY);

    }



    public String getName() {

        return getString(NAME_KEY);

    }



    public void setId(String userId) {

        put(ID_KEY, userId);

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

    public List<User> getFriends() {
        return getList(FRIENDS);
    }

    public void setFriends(List<User> friends) {
        put(FRIENDS, friends);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public User() {
    }

    protected User(Parcel in) {
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
