package com.example.rescueone.sos;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GPSListener implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {
        String provider = location.getProvider();
        double altitude = location.getAltitude();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}