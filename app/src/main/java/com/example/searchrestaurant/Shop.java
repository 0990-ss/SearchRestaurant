package com.example.searchrestaurant;

public class Shop {
    private String name;
    private String address;
    private double lat;
    private double lng;
    private Photo photo;
    private String access;
    private String open;

    public String getName() { return name; }
    public String getAddress() { return address; }
    public Photo getPhoto() { return photo; }
    public String getAccess() { return access;}
    public String getOpen() { return open; }
}
