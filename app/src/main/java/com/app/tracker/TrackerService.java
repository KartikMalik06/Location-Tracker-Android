package com.app.tracker;


import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Random;


public class TrackerService extends Service {
    private static TrackerService attendanceService;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000*60;

    private static final float DISPLACEMENT=100f;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =1000*40;
    private LocationRequest mLocationRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
        attendanceService = this;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        fetchLocation();
        return START_STICKY;
    }

    private void fetchLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Preference.get(TrackerService.this).add(new LocationData(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude(), Calendar.getInstance().getTimeInMillis()));
               // Log.d("TrackerApp","Lat "+locationResult.getLastLocation().getLatitude()+" lng "+locationResult.getLastLocation().getLongitude());
               // onNewLocation(locationResult.getLastLocation());

            }
        };

        createLocationRequest();
        requestLocationUpdates();
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e("TreackerApp", "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }



    public static TrackerService getService() {
        return attendanceService;
    }



    private void startForegroundService() {
        Notification notification = NotificationHelper.getNotificationBuilder(this, true, getString(R.string.service_message), MainActivity.class, null, NotificationHelper.GROUP_SERVICE);
        startForeground(new Random().nextInt(), notification);
    }

    public static void stopForegroundService(Context context) {
        Intent myService = new Intent(context, TrackerService.class);
        context.stopService(myService);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationHelper.cancel(this);
        stopSelf();
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
        }
        attendanceService = null;
        stopForeground(true);
    }

}
