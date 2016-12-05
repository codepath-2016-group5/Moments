package com.codepath.apps.findmate.models;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ParseClassName("Group")
public class Group extends ParseObject {

    public static final String NAME_KEY = "name";
    public static final String INVITE_KEY = "invite";
    public static final String MEMBERS_KEY = "members";
    public static final String CHECK_INS_KEY = "checkins";

    private static final Random RANDOM = new Random();

    public static void getGroupsByUser(ParseUser user, FindCallback<Group> callback) {
        ParseQuery.getQuery(Group.class)
                .include(Group.MEMBERS_KEY)
                .include(Group.CHECK_INS_KEY)
                .include("checkins.place")
                .include("checkins.creator")
                .whereEqualTo(Group.MEMBERS_KEY, user)
                .findInBackground(callback);
    }

    public static void getGroupByInviteCode(String inviteCode, FindCallback<Group> callback) {
        ParseQuery.getQuery(Group.class)
                .include(Group.MEMBERS_KEY)
                .include(Group.CHECK_INS_KEY)
                .include("checkins.place")
                .include("checkins.creator")
                .whereEqualTo(Group.INVITE_KEY, inviteCode)
                .findInBackground(callback);
    }

    public static void getGroupById(String id, GetCallback<Group> callback) {
        ParseQuery.getQuery(Group.class)
                .include(Group.MEMBERS_KEY)
                .include(Group.CHECK_INS_KEY)
                .include("checkins.place")
                .include("checkins.creator")
                .getInBackground(id, callback);
    }

    public Group() {
    }

    public String getName() {
        return getString(NAME_KEY);
    }

    public String getInviteCode() {
        return getString(INVITE_KEY);
    }

    public List<ParseUser> getMembers() {
        return getList(MEMBERS_KEY);
    }

    public List<CheckIn> getCheckIns() {
        List<CheckIn> checkIns = getList(CHECK_INS_KEY);
        return checkIns == null ? new ArrayList<CheckIn>() : checkIns;
    }

    public Group setName(String name) {
        put(NAME_KEY, name);
        return this;
    }

    public Group setInviteCode() {
        put(INVITE_KEY, randomInviteCode());
        return this;
    }

    public Group addMember(ParseUser member) {
        add(MEMBERS_KEY, member);
        return this;
    }

    public Group addCheckIn(CheckIn checkIn) {
        add(CHECK_INS_KEY, checkIn);
        return this;
    }

    // Generate a random six-digit string
    private static String randomInviteCode() {
        return Integer.toString(100000 + RANDOM.nextInt(900000));
    }
}
