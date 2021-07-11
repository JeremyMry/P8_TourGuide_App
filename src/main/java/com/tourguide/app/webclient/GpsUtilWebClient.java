package com.tourguide.app.webclient;

import com.tourguide.app.models.Attraction;
import com.tourguide.app.models.Location;
import com.tourguide.app.models.VisitedLocation;
import com.tourguide.app.service.TourGuideService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);

    private final String BASE_URL_LOCALHOST = "http://localhost:8081";
    private final String PATH_USER_LOCATION = "/getUserLocation";
    private final String PATH_ALL_ATTRACTIONS = "/getAllAttractions";
    private final String USER = "?user=";

    private final String getUserLocationUri() { return BASE_URL_LOCALHOST + PATH_USER_LOCATION; }
    private final String getAttractionUri() { return BASE_URL_LOCALHOST + PATH_ALL_ATTRACTIONS; }

    public VisitedLocation getUserLocation(UUID user) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        ResponseEntity<VisitedLocation> result = restTemplate.getForEntity(getUserLocationUri() + USER + user, VisitedLocation.class);

        VisitedLocation visitedLocation;

        visitedLocation = result.getBody();
        logger.debug(String.valueOf(visitedLocation));
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
}
