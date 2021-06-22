package com.tourguide.app.models;

public class NearbyAttractions {

    private String attractionName;

    private Location attractionLocation;

    private Location touristLocation;

    private Double distanceBetweenTouristAndAttraction;

    private Integer rewardPoint;

    public NearbyAttractions() {
    }

    public NearbyAttractions(String attractionName, Location attractionLocation, Location touristLocation, Double distanceBetweenTouristAndAttraction, Integer rewardPoint) {
        this.attractionName = attractionName;
        this.attractionLocation = attractionLocation;
        this.touristLocation = touristLocation;
        this.distanceBetweenTouristAndAttraction = distanceBetweenTouristAndAttraction;
        this.rewardPoint = rewardPoint;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public Location getAttractionLocation() {
        return attractionLocation;
    }

    public void setAttractionLocation(Location attractionLocation) {
        this.attractionLocation = attractionLocation;
    }

    public Location getTouristLocation() {
        return touristLocation;
    }

    public void setTouristLocation(Location touristLocation) {
        this.touristLocation = touristLocation;
    }

    public Double getDistanceBetweenTouristAndAttraction() {
        return distanceBetweenTouristAndAttraction;
    }

    public void setDistanceBetweenTouristAndAttraction(Double distanceBetweenTouristAndAttraction) {
        this.distanceBetweenTouristAndAttraction = distanceBetweenTouristAndAttraction;
    }

    public Integer getRewardPoint() {
        return rewardPoint;
    }

    public void setRewardPoint(Integer rewardPoint) {
        this.rewardPoint = rewardPoint;
    }
}