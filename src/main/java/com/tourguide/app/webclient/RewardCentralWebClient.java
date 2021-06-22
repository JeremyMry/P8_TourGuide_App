package com.tourguide.app.webclient;

import com.tourguide.app.models.VisitedLocation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.UUID;

@Service
public class RewardCentralWebClient {

    private final String BASE_URL = "http://RewardCentral:8083";
    private final String BASE_URL_LOCALHOST = "http://localhost:8083";
    private final String PATH_GET_REWARD_POINTS = "/getRewardPoints";
    private final String ATTRACTION = "?attractionId=";
    private final String USER = "?user=";

    private final String getRewardPointsUri() { return BASE_URL + PATH_GET_REWARD_POINTS; }

    public int getRewardPoints(UUID attractionId, UUID user) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Integer> result = restTemplate.getForEntity(getRewardPointsUri() + ATTRACTION + attractionId + USER + user, Integer.class);

        int rewardPoints;
        rewardPoints = result.getBody();

        return rewardPoints;
    }

}
