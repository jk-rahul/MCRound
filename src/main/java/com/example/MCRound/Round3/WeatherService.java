package com.example.MCRound.Round3;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherService {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "9c339dc4d2259af792cec32d41288601"; // Replace with your OpenWeatherMap API key
    //String url = BASE_URL + "?lat=40.714728&lon=-73.998672&appid=" + apiKey + "&units=metric";


    public static void getWeather(double lat, double lon) {
        String url = String.format("%s?lat=%f&lon=%f&appid=%s&units=metric", BASE_URL, lat, lon, API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        WeatherService.getWeather(28.7383, 77.0822);
    }
}
