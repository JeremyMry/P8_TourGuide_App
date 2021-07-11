package com.tourguide.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourguide.app.helper.InternalTestHelper;
import com.tourguide.app.models.Attraction;
import com.tourguide.app.models.User;
import com.tourguide.app.models.UserReward;
import com.tourguide.app.models.VisitedLocation;
import com.tourguide.app.service.TourGuideService;
import com.tourguide.app.webclient.GpsUtilWebClient;
import com.tourguide.app.webclient.RewardCentralWebClient;
import com.tourguide.app.webclient.TripPricerWebClient;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPerformance {

    /*
     * A note on performance improvements:
     *
     *     The number of users generated for the high volume tests can be easily adjusted via this method:
     *
     *     		InternalTestHelper.setInternalUserNumber(100000);
     *
     *
     *     These tests can be modified to suit new solutions, just as long as the performance metrics
     *     at the end of the tests remains consistent.
     *
     *     These are performance metrics that we are trying to hit:
     *
     *     highVolumeTrackLocation: 100,000 users within 15 minutes:
     *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
     *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     */

    @Test
    @Async
    public void highVolumeTrackLocation() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();

        // Users should be incremented up to 100,000, and test finishes within 15 minutes
        InternalTestHelper.setInternalUserNumber(100000);
        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        List<User> allUsers = tourGuideService.getAllUsers();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(44);

        for (User user: allUsers) {
            Runnable runnable = () -> {
                VisitedLocation visitedLocation = gpsUtilWebClient.getUserLocation(user.getUserId());
                user.addToVisitedLocations(visitedLocation);
            };
            executorService.execute(runnable);
        }
        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.MINUTES);
        }
		catch (InterruptedException interruptedException) {
        }

        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    @Test
    public void highVolumeGetRewards() {
        GpsUtilWebClient gpsUtilWebClient = new GpsUtilWebClient();
        RewardCentralWebClient rewardCentralWebClient = new RewardCentralWebClient();
        TripPricerWebClient tripPricerWebClient = new TripPricerWebClient();

        // Users should be incremented up to 100,000, and test finishes within 15 minutes
        InternalTestHelper.setInternalUserNumber(100000);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        TourGuideService tourGuideService = new TourGuideService(gpsUtilWebClient, rewardCentralWebClient, tripPricerWebClient);

        List<User> allUsers = tourGuideService.getAllUsers();

        Attraction attractionList = gpsUtilWebClient.getAttractions().get(0);

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(44);

            //Execute the code as per in the method "trackUserLocation" in TourGuideService
            for (User user: allUsers) {
                Runnable runnable = () -> {
                    user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attractionList, new Date()));

                    List<VisitedLocation> userLocations = user.getVisitedLocations();
                    List<Attraction> attractions = gpsUtilWebClient.getAttractions();
                    for(VisitedLocation visitedLocation : userLocations) {
                        for(Attraction attraction : attractions) {
                            if(user.getUserReward().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
                                if(tourGuideService.nearAttraction(visitedLocation, attraction)) {
                                    user.addUserReward(new UserReward(visitedLocation, attraction, rewardCentralWebClient.getRewardPoints(attraction.attractionId, user.getUserId())));
                                }
                            }
                        }
                    }

                    assertTrue(user.getUserReward().size() > 0);
                };
                executorService.execute(runnable);
            }
            executorService.shutdown();
            executorService.awaitTermination(15, TimeUnit.MINUTES);

        }
        catch (InterruptedException interruptedException) {
        }

        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

}