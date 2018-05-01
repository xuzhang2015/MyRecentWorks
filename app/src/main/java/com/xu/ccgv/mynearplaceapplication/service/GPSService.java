package com.xu.ccgv.mynearplaceapplication.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.xu.ccgv.mynearplaceapplication.R;
import com.xu.ccgv.mynearplaceapplication.Utils.UtilsMethod;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * provide the Location data
 */

public class GPSService extends Service {
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClientLocation;
    private LocationRequest mLocationRequest;
    private String provider;
    private boolean isGPSEnabled = false;
    private LocationListener locationListener = new com.google.android.gms.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (null != location) {
                //send the msg to the MainActivity
                EventBus.getDefault().post(location);
            }

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //get the provider
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            UtilsMethod.showToast(R.string.no_location_provider_to_use);
            return;
        }
        isGPSEnabled = locationManager.isProviderEnabled(provider);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //get the GoogleApiClient ready
        if (isGPSEnabled) {
            buildGoogleApiClient();
            if (!mGoogleApiClientLocation.isConnected()) {
                mGoogleApiClientLocation.connect();
            }
        }
        return START_STICKY;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClientLocation = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        createLocationRequest();
                        startLocationUpdates();
                        if (ActivityCompat.checkSelfPermission(GPSService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GPSService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        Location location = locationManager.getLastKnownLocation(provider);
                        if (null != location) {
                            EventBus.getDefault().post(location);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClientLocation.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                }).addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();


    }

    //GPS updating configs
    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        int UPDATE_INTERVAL = 2000;
        int FATEST_INTERVAL = 1000;
        int DISPLACEMENT = 10;
        //
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClientLocation, mLocationRequest, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClientLocation, locationListener);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClientLocation.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClientLocation.disconnect();
        }
    }
}
