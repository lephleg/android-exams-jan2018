package com.example.lephleg.androidexams;

public class Place {

    private String id;
    private String name;
    private String fullAddress;
    private double rating;
    private boolean hasOpeningHoursInfo;
    private boolean openNow;


    public Place (String id, String name, String address, Boolean openNow, Double rating) {
        this.id = id;
        this.name = name;
        this.fullAddress = address;
        this.rating = rating;
        this.hasOpeningHoursInfo = true;
        this.openNow = openNow;
    }

    public Place (String id, String name, String address, Double rating) {
        this.id = id;
        this.name = name;
        this.fullAddress = address;
        this.rating = rating;
        this.hasOpeningHoursInfo = false;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.fullAddress;
    }

    public String getOpenNowDescr() {

        if (hasOpeningHoursInfo) {
            if (this.openNow) {
                return "Open now!";
            } else {
                return "Closed now";
            }
        } else {
            return "";
        }
    }

    public String getRatingDescr(){

        return Double.toString(this.rating) + " stars";
    }

    @Override
    public String toString() {
        String open =  (hasOpeningHoursInfo) ? ". Open Now? "+openNow : "";
        return name+" Rating: "+rating + " "+open;
    }
}
