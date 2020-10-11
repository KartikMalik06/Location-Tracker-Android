package com.app.tracker;

public class LocationData {

    double lat;
    double lng;
    long time;


    LocationData(double lat,double lng,long time){
        this.lat=lat;
        this.lng=lng;
        this.time=time;
    }


    public long getTime() {
        return time;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
