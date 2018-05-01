package com.xu.ccgv.mynearplaceapplication.Bean;

import android.graphics.Bitmap;
import android.location.Location;

/**
 * Created by xz on 29/04/2018.
 */

public class LocationEntity {
    private Location location;
    private String icon_uri;
    private Bitmap icon_bm;
    private String name;
    private String vicinity;
    private String type;
    //
    private String direction;
    private double distance;
    public LocationEntity(Location location,
                          String icon_uri,
                          Bitmap icon_bm,
                          String name,
                          String vicinity,
                          String type) {
        this.location = location;
        this.icon_uri = icon_uri;
        this.icon_bm = icon_bm;
        this.name = name;
        this.vicinity = vicinity;
        this.type = type;
    }
    public LocationEntity() {

    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getIcon_uri() {
        return icon_uri;
    }

    public void setIcon_uri(String icon_uri) {
        this.icon_uri = icon_uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Bitmap getIcon_bm() {
        return icon_bm;
    }

    public void setIcon_bm(Bitmap icon_bm) {
        this.icon_bm = icon_bm;
    }
}
