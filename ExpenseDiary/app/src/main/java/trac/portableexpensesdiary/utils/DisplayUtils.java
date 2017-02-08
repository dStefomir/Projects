package trac.portableexpensesdiary.utils;

import android.app.Activity;
import android.content.Intent;

public class DisplayUtils {

    public static void startNewActivity(
            Activity currentActivity,
            Intent intent,
            boolean shouldFinishCurrentActivity) {

        currentActivity.startActivity(intent);

        if (shouldFinishCurrentActivity) {
            currentActivity.finish();
        }
    }
}
