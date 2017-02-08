package trac.portableexpensesdiary.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class LocationUtils {

    private Timer timer;
    private LocationManager locationManager;
    private LocationResult locationResult;
    private boolean isGpsEnabled = false;
    private boolean isNetworkEnabled = false;


    private LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();

            locationResult.gotLocation(location);

            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);
        }

        public void onProviderDisabled(String provider) {}

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();

            locationResult.gotLocation(location);

            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);
        }

        public void onProviderDisabled(String provider) {}

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    public boolean getLocation(Context context, LocationResult result) {
        locationResult = result;

        if (locationManager == null) {
            locationManager =
                    (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            isGpsEnabled =
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();

            isGpsEnabled = false;
        }

        try {
            isNetworkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();

            isNetworkEnabled = false;
        }

        if (!isGpsEnabled && !isNetworkEnabled) {

            return false;
        }

        if (isGpsEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListenerGps
            );
        }

        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0,
                    locationListenerNetwork
            );
        }

        timer = new Timer();
        timer.schedule(
                new LastLocationTimerTask(),
                20000
        );

        return true;
    }

    private class LastLocationTimerTask extends TimerTask {

        @Override
        public void run() {
            locationManager.removeUpdates(locationListenerGps);
            locationManager.removeUpdates(locationListenerNetwork);

            Location networkLocation = null, gpsLocation = null;

            if (isGpsEnabled) {
                gpsLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (isNetworkEnabled) {
                networkLocation =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (gpsLocation != null && networkLocation != null) {

                if (gpsLocation.getTime() > networkLocation.getTime()) {
                    locationResult.gotLocation(gpsLocation);
                } else {
                    locationResult.gotLocation(networkLocation);
                }

                return;
            }

            if (gpsLocation != null) {
                locationResult.gotLocation(gpsLocation);

                return;
            }

            if (networkLocation != null) {
                locationResult.gotLocation(networkLocation);

                return;
            }

            locationResult.gotLocation(null);
        }
    }

    public static abstract class LocationResult {

        public abstract void gotLocation(Location location);
    }
}