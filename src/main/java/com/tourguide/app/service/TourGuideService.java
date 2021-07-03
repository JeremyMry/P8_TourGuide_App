package com.tourguide.app.service;

import com.tourguide.app.helper.InternalTestHelper;
import com.tourguide.app.models.*;
import com.tourguide.app.tracker.Tracker;
import com.tourguide.app.webclient.GpsUtilWebClient;
import com.tourguide.app.webclient.RewardCentralWebClient;
import com.tourguide.app.webclient.TripPricerWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service
public class TourGuideService {

    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    private final GpsUtilWebClient gpsUtilWebClient;
    private final RewardCentralWebClient rewardCentralWebClient;
    private final TripPricerWebClient tripPricerWebClient;
    public final Tracker tracker;
    boolean testMode = true;

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private int proximityBuffer = 10;

    public TourGuideService(GpsUtilWebClient gpsUtilWebClient, RewardCentralWebClient rewardCentralWebClient, TripPricerWebClient tripPricerWebClient) {
        this.gpsUtilWebClient = gpsUtilWebClient;
        this.rewardCentralWebClient = rewardCentralWebClient;
        this.tripPricerWebClient = tripPricerWebClient;

        if(testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    public List<UserReward> getUserRewards(User user) {
        return user.getUserReward();
    }

    public VisitedLocation getUserLocation(User user) {
        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
        return visitedLocation;
    }

    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(internalUserMap.values());
    }

    public void addUser(User user) {
        if(!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    public List<Provider> getTripDeals(User user) {
        List<Provider> providers = tripPricerWebClient.getPrice(tripPricerApiKey, user);
        user.setTripDeals(providers);
        return providers;
    }

    public VisitedLocation trackUserLocation(User user) {
        VisitedLocation visitedLocation = gpsUtilWebClient.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        calculateRewards(user);
        return visitedLocation;
    }

    public void trackUserLocationList(List<User> userList) {
        logger.debug("Track user location for user list : nbUsers = " + userList.size());
        ExecutorService trackLocationExecutorService = Executors.newFixedThreadPool(1500);

        userList.forEach(user -> {
            Runnable runnableTask = () -> {

                trackUserLocation(user);
            };
            trackLocationExecutorService.submit(runnableTask);
        });

        trackLocationExecutorService.shutdown();
    }

    public List<NearbyAttractions> getNearByAttractions(VisitedLocation visitedLocation) {

        Map<Attraction, Double> attractionMap = new HashMap<>();
        for(Attraction attraction : gpsUtilWebClient.getAttractions()) {
            attractionMap.put(attraction, getDistance(attraction, visitedLocation.location));
        }

        List<Map.Entry<Attraction, Double>> list = new ArrayList<>(attractionMap.entrySet());
        list.sort(Map.Entry.comparingByValue());

        List<NearbyAttractions> nearbyAttractionsList = new ArrayList<>();

        for (int i=0; i<=4; i++) {
            NearbyAttractions nearbyAttractionsObject = new NearbyAttractions();
            nearbyAttractionsObject.setAttractionName(list.get(i).getKey().attractionName);
            nearbyAttractionsObject.setRewardPoint(rewardCentralWebClient.getRewardPoints(list.get(i).getKey().attractionId, visitedLocation.userId));
            nearbyAttractionsObject.setTouristLocation(visitedLocation.location);
            nearbyAttractionsObject.setDistanceBetweenTouristAndAttraction(list.get(i).getValue());
            nearbyAttractionsObject.setAttractionLocation(new Location(list.get(i).getKey().latitude, list.get(i).getKey().longitude));
            nearbyAttractionsList.add(nearbyAttractionsObject);
        }

        return nearbyAttractionsList;
    }

    public HashMap<UUID, Location> getAllUsersLastLocation() {
        HashMap<UUID, Location> usersLastLocationList = new HashMap<>();
        for(User user: getAllUsers()) {
            usersLastLocationList.put(user.getUserId(), user.getLastVisitedLocation().location);
        }
        return usersLastLocationList;
    }

    private int getRewardPoints(Attraction attraction, User user) {
        return rewardCentralWebClient.getRewardPoints(attraction.attractionId, user.getUserId());
    }

    public void calculateRewards(User user) {
        List<VisitedLocation> userLocations = user.getVisitedLocations();
        List<Attraction> attractions = gpsUtilWebClient.getAttractions();
        for(VisitedLocation visitedLocation : userLocations) {
            for(Attraction attraction : attractions) {
                if(user.getUserReward().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
                    if(nearAttraction(visitedLocation, attraction)) {
                            user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
                    }
                }
            }
        }
    }

    public void calculateRewardsList(List<User> userList) {
        logger.debug("Track user location for user list : nbUsers = " + userList.size());
        ExecutorService rewardsExecutorService = Executors.newFixedThreadPool(1500);

        userList.forEach(user -> {
            Runnable runnableTask = () -> {

                calculateRewards(user);
            };
            rewardsExecutorService.submit(runnableTask);
        });

        rewardsExecutorService.shutdown();
    }

    public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return !(getDistance(visitedLocation.location, attraction) > proximityBuffer);
    }

    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();
    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i-> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }
}
