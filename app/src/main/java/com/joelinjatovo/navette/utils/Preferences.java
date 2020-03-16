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
            setCurrentUser(context, (long) user.getId());
        }

        public static void setCurrentUser(Context context, Long userId) {
            SharedPreferences.Editor editor = getPreferences(context).edit();
            editor.putLong(USER, userId);
            editor.apply();
        }

        public static Long getCurrentUser(Context context) {
            return getPreferences(context).getLong(USER, 0);
        }
    }
}
