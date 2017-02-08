package trac.portableexpensesdiary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class PermissionUtils {

    public static boolean checkForCameraPermission(Activity activity) {
        boolean isCameraPermissionGranted = false;

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            isCameraPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            Manifest.permission.CAMERA,
                    },
                    Constants.REQUEST_CAMERA
            );
        }

        return isCameraPermissionGranted;
    }

    public static boolean checkForStoragePermission(Activity activity) {
        boolean isStoragePermissionGranted = false;

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            isStoragePermissionGranted = true;
        } else {

            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    Constants.REQUEST_CAMERA
            );
        }

        return isStoragePermissionGranted;
    }

    public static boolean areAllPermissionsGranted(Activity activity) {
        boolean arePermissionGranted = false;

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            arePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    Constants.REQUEST_CAMERA
            );
        }

        return arePermissionGranted;
    }

    public static boolean isGpsPermissionGranted(Activity activity) {
        boolean arePermissionGranted = false;

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            arePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    Constants.REQUEST_CAMERA
            );
        }

        return arePermissionGranted;
    }
}
