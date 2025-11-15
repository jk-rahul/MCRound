package com.example.MCRound.Round3;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class CountryCapitalCityService {
    private static final String BASE_URL = "https://jsonmock.hackerrank.com/api/countries?name=";

    public static String getCapitalCity(String country) {
        String url = String.format("%s%s", BASE_URL, country);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        String capital;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            capital = extractCapital(httpResponse.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return capital;
    }

    /**
     * Check BestRatedTvShowInGenreService for async HTTPClient request.
     * Implementation uses
     *  HTTPClient (both sync and async) +
     *  CompletableFuture +
     *  AtomicReference +
     *  CompletableFuture.allOf().join()
     * @param country
     * @return
     */
    public static String getCapitalCityAsync(String country) {
        String url = String.format("%s%s", BASE_URL, country);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        AtomicReference<String> capital = new AtomicReference<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            CompletableFuture<Void> futureResponse = httpClient.sendAsync(httpRequest,
                    HttpResponse.BodyHandlers.ofString())
                            .thenAccept(httpResponse -> {
                                String capitalResponse = extractCapital(httpResponse.body());
                                capital.getAndUpdate(existing -> capitalResponse);
                            });
            futures.add(futureResponse);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return capital.get();
    }

    private static String extractCapital(String httpResponseBody) {
        JsonArray data = JsonParser.parseString(httpResponseBody)
                            .getAsJsonObject()
                            .getAsJsonArray("data");
        if (data.isEmpty()) return null;
        JsonObject countryData = data.get(0).getAsJsonObject();
        return countryData.get("capital").getAsString();
    }

    public static void main(String[] args) {
        System.out.println(CountryCapitalCityService.getCapitalCity("Thailand"));
        System.out.println(CountryCapitalCityService.getCapitalCityAsync("Vietnam"));
        System.out.println(CountryCapitalCityService.getCapitalCityAsync("Cambodia"));
        System.out.println(CountryCapitalCityService.getCapitalCityAsync("Algeria"));
        System.out.println(CountryCapitalCityService.getCapitalCityAsync("Denmark"));
        System.out.println(CountryCapitalCityService.getCapitalCityAsync("Incorrect"));
    }
}
