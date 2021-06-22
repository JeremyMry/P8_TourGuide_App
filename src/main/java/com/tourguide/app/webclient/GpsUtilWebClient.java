package com.tourguide.app.webclient;

import com.tourguide.app.models.Attraction;
import com.tourguide.app.models.Location;
import com.tourguide.app.models.VisitedLocation;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class GpsUtilWebClient {

    private final String BASE_URL = "http://GpsUtil:8081";
    private final String BASE_URL_LOCALHOST = "http://localhost:8081";
    private final String PATH_USER_LOCATION = "/getUserLocation";
    private final String PATH_ALL_ATTRACTIONS = "/getAllAttractions";
    private final String PATH_ATTRACTIONS_PROXIMITY = "/isWithinAttractionProximity";
    private final String PATH_NEAR_ATTRACTION = "/isNearAttraction";
    private final String PATH_GET_DISTANCE = "/getDistance";
    private final String USER = "?user=";

    private final String getUserLocationUri() { return BASE_URL + PATH_USER_LOCATION; }
    private final String getAttractionUri() { return BASE_URL + PATH_ALL_ATTRACTIONS; }
    private final String getAttractionProximityUri() { return BASE_URL + PATH_ATTRACTIONS_PROXIMITY; }
    private final String nearAttractionUri() { return BASE_URL + PATH_NEAR_ATTRACTION; }
    private final String getDistanceUri() { return BASE_URL + PATH_GET_DISTANCE; }

    public VisitedLocation getUserLocation(UUID user) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        ResponseEntity<VisitedLocation> result = restTemplate.getForEntity(getUserLocationUri() + USER + user, VisitedLocation.class);

        VisitedLocation visitedLocation;
        visitedLocation = result.getBody();

        return visitedLocation;
    }

    public List<Attraction> getAttractions() {
        RestTemplate restTemplate = new RestTemplate();
        List<Attraction> attractions;

        ResponseEntity<List<Attraction>> result = restTemplate.exchange(getAttractionUri(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Attraction>>() {
        });
        attractions = result.getBody();
        return attractions;
    }

    public Boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return false;
    }

    public Double getDistance(Attraction attraction, Location location) {
        return 2D;
    }

}
