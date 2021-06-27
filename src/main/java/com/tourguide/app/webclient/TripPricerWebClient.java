package com.tourguide.app.webclient;

import com.tourguide.app.models.Attraction;
import com.tourguide.app.models.Provider;
import com.tourguide.app.models.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TripPricerWebClient {

    private final String BASE_URL_LOCALHOST = "http://localhost:8082";
    private final String PATH_GET_PRICE = "/getPrice";
    private final String USER = "&user=";
    private final String APIKEY = "?apiKey=";

    private final String getPriceUri() { return BASE_URL_LOCALHOST + PATH_GET_PRICE; }

    public List<Provider> getPrice(String apiKey, User user) {
        RestTemplate restTemplate = new RestTemplate();
        List<Provider> providers;

        ResponseEntity<List<Provider>> result = restTemplate.exchange(getPriceUri() + APIKEY + apiKey + USER + user, HttpMethod.GET, null, new ParameterizedTypeReference<List<Provider>>() {
        });
        providers = result.getBody();
        return providers;
    }

}
