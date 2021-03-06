package com.tourguide.app.controller;

import com.jsoniter.output.JsonStream;
import com.tourguide.app.models.Attraction;
import com.tourguide.app.models.Location;
import com.tourguide.app.service.TourGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserLocation(tourGuideService.getUser(userName)).location);
    }

    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getNearByAttractions(tourGuideService.getUserLocation(tourGuideService.getUser(userName))));
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(tourGuideService.getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public HashMap<UUID, Location> getAllCurrentLocations() {
        return tourGuideService.getAllUsersLastLocation();
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getTripDeals(tourGuideService.getUser(userName)));
    }
}