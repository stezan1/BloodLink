package com.example.bloodlink;

public class LocationData {
    static String deviceId ;
    private String username;
    private double latitude;
    private double longitude;

    // Create getters and setters for the fields
    // ...

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



}