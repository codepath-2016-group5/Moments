package com.codepath.apps.findmate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject {

    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";

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
}
