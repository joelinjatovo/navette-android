package com.joelinjatovo.navette.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.joelinjatovo.navette.database.entity.User;

public class Preferences {

    public static final String USER = "user";

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static class Auth{

        public static void setCurrentUser(Context context, User user) {
            setCurrentUser(context, user.getId());
        }

        public static void setCurrentUser(Context context, Integer userId) {
            SharedPreferences.Editor editor = getPreferences(context).edit();
            editor.putInt(USER, userId);
            editor.apply();
        }

        public static Integer getCurrentUser(Context context) {
            return getPreferences(context).getInt(USER, 0);
        }
    }
}
