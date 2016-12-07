package com.codepath.apps.findmate.utils;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.codepath.apps.findmate.models.ParseUsers;
import com.parse.ParseUser;

public final class DrawableUtils {

    public static int getColor(String key) {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        return generator.getColor(key);
    }

    public static TextDrawable getInitialsDrawable(ParseUser user) {
        String name = ParseUsers.getName(user);
        char initial = name.isEmpty() ? name.charAt(0) : user.getEmail().charAt(0);
        int color = getColor(name);

        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .toUpperCase()
                .endConfig()
                .round();

        return builder.build(Character.toString(initial), color);
    }
}
