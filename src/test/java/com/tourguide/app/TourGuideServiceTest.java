package com.tourguide.app;

import com.tourguide.app.helper.InternalTestHelper;
import com.tourguide.app.models.*;
import com.tourguide.app.service.TourGuideService;
import com.tourguide.app.webclient.GpsUtilWebClient;
import com.tourguide.app.webclient.RewardCentralWebClient;
import com.tourguide.app.webclient.TripPricerWebClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TourGuideServiceTest {


    @Test
    public void getUserReward() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();
        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        Attraction attraction = gpsUtilWebClient.getAttractions().get(0);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));

        tourGuideService.trackUserLocation(user);

        List<UserReward> userRewards = tourGuideService.getUserRewards(user);

        assertTrue(userRewards.size() == 1);
    }

    @Test
    public void getUserLocation() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();
        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        assertTrue(visitedLocation.userId.equals(user.getUserId()));
    }

    @Test
    public void getUser() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();
        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user);

        assertTrue(tourGuideService.getUser("jon") == user);
    }

    @Test
    public void getAllUsers() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();
        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        List<User> allUsers = tourGuideService.getAllUsers();

        assertTrue(allUsers.contains(user));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void addUser() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();
        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        User retrivedUser = tourGuideService.getUser(user.getUserName());
        User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

        assertEquals(user, retrivedUser);
        assertEquals(user2, retrivedUser2);
    }

    @Test
    public void getTripDeals() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();
        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        List<Provider> providers = tourGuideService.getTripDeals(user);

        assertEquals(5, providers.size());
    }

    @Test
    public void trackUserLocation() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();
        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        assertEquals(user.getUserId(), visitedLocation.userId);
        assertFalse(user.getVisitedLocations().isEmpty());
    }

    @Test
    public void getNearbyAttractions() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();
        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        List<NearbyAttractions> attractions = tourGuideService.getNearByAttractions(visitedLocation);

        assertEquals(5, attractions.size());
    }

    @Test
    public void getAllUsersLastLocation() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();

        InternalTestHelper.setInternalUserNumber(3);

        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);


        HashMap<UUID, Location> usersLastLocation = tourGuideService.getAllUsersLastLocation();

        tourGuideService.tracker.stopTracking();

        assertEquals(3, usersLastLocation.size());
    }

    @Test
    public void calculateRewards() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();

        InternalTestHelper.setInternalUserNumber(2);

        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        List<User> allUsers = tourGuideService.getAllUsers();

        Attraction attractionList = gpsUtilWebClient.getAttractions().get(0);

        for(User user: allUsers) {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attractionList, new Date()));
            tourGuideService.calculateRewards(user);
            assertEquals(user.getUserReward().size(), 1);
            assertFalse(user.getUserReward().isEmpty());
        }
    }

    @Test
    public void nearAllAttractions() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();

        InternalTestHelper.setInternalUserNumber(1);

        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        tourGuideService.setProximityBuffer(Integer.MAX_VALUE);

        tourGuideService.tracker.stopTracking();

        tourGuideService.calculateRewards(tourGuideService.getAllUsers().get(0));
        List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));


        assertEquals(gpsUtilWebClient.getAttractions().size(), userRewards.size());
    }


    @Test
    public void nearAttraction() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();
        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Double latitude = 33.817595D;
        Double longitude = -117.922008D;
        Double latitude1 = 733.817595D;
        Double longitude1 = 117.922008D;
        Location location = new Location(latitude, longitude);
        Location location1 = new Location(latitude1, longitude1);
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date());
        VisitedLocation visitedLocation1 = new VisitedLocation(user.getUserId(), location1, new Date());
        Attraction attraction = new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D);

        Assertions.assertTrue(tourGuideService.nearAttraction(visitedLocation, attraction));
        Assertions.assertFalse(tourGuideService.nearAttraction(visitedLocation1, attraction));
    }

    @Test
    public void getDistance() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();
        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        Double latitude = 33.817595D;
        Double longitude = -117.922008D;
        Double latitude1 = 34.817595D;
        Location location = new Location(latitude, longitude);
        Location location1 = new Location(latitude1, longitude);

        Assertions.assertEquals(tourGuideService.getDistance(location1, location), 69.0467669999931);
    }
}
