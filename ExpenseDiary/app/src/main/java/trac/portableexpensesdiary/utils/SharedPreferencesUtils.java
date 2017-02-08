package trac.portableexpensesdiary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesUtils {

    public static void setBooleanData(Context context, boolean value, String tag) {
        getSharedPreferences(context).edit()
                .putBoolean(
                        tag,
                        value
                ).apply();
    }

    public static void setStringData(Context context, String value, String tag) {
        getSharedPreferences(context).edit()
                .putString(
                        tag,
                        value
                ).apply();
    }

    public static boolean getBooleanData(Context context, String tag) {

        return getSharedPreferences(context).getBoolean(
                tag,
                false
        );
    }

    public static String getStringData(Context context, String tag) {

        return getSharedPreferences(context).getString(
                tag,
                ""
        );
    }

    private static SharedPreferences getSharedPreferences(Context context) {

        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
