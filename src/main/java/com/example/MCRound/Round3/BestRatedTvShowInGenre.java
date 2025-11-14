package com.example.MCRound.Round3;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class BestRatedTvShowInGenre {
    private static final String BASE_URL = "https://jsonmock.hackerrank.com/api/tvseries?page=";

    public static String getBestShowInGenre(String genre) {
        AtomicReference<Map.Entry<String, Double>> bestShowInGenre = new AtomicReference<>();
        final Gson gson = new Gson();
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            // Step 1: Sync calls for first page to find total pages
            String url1 = String.format("%s%d", BASE_URL, 1);
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(URI.create(url1))
                    .GET()
                    .build();
            HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
            TvSeriesResponse tvSeriesResponse1 = gson.fromJson(response1.body(), TvSeriesResponse.class);
            updateBestInGenre(tvSeriesResponse1, genre, bestShowInGenre);
            Long total_pages = tvSeriesResponse1.total_pages;

            // Step 2: Async calls for rest of pages
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (int page = 2; page <= total_pages; page++) {
                String url2 = String.format("%s%d", BASE_URL, page);
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create(url2))
                        .GET()
                        .build();
                CompletableFuture<Void> futureResponse = httpClient.sendAsync(request2,
                                HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> {
                            TvSeriesResponse tvSeriesResponse2 = gson.fromJson(response.body(), TvSeriesResponse.class);
                            updateBestInGenre(tvSeriesResponse2, genre, bestShowInGenre);
                        });
                futures.add(futureResponse);
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
        }
        if (bestShowInGenre.get() == null) return "";
        return bestShowInGenre.get().getKey();
    }

    private static void updateBestInGenre(TvSeriesResponse tvSeriesResponse, String genre,
                                          AtomicReference<Map.Entry<String, Double>> bestShowInGenre) {
        final Map.Entry<String, Double> ans = getTopRatedShowInResponse(tvSeriesResponse, genre);
        if (ans == null) return;
        bestShowInGenre.getAndUpdate(existing -> {
            if (existing == null || ans.getValue() > existing.getValue()
                || (ans.getValue().equals(existing.getValue()) && ans.getKey().compareTo(existing.getKey()) < 0)) {
                return ans;
            }
            return existing;
        });
    }

    private static Map.Entry<String, Double> getTopRatedShowInResponse(TvSeriesResponse tvSeriesResponse, String genre) {
        Map.Entry<String, Double> ans = null;
        for (TvSeriesResponse.Data showData : tvSeriesResponse.data) {
            if (showData.genre.contains(genre)) {
                if (ans == null || showData.imdb_rating > ans.getValue()
                    || (showData.imdb_rating == ans.getValue() && showData.name.compareTo(ans.getKey()) < 0)) {
                    ans = Map.entry(showData.name, showData.imdb_rating);
                }
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        String[] genres = new String[]{"Action", "Comedy", "Mystery", "Horror", "Anime"};
        for (String genre : genres) {
            long startTime = LocalTime.now().toNanoOfDay();
            String topRatedShow = BestRatedTvShowInGenre.getBestShowInGenre(genre);
            long executionTimeInMillis = (LocalTime.now().toNanoOfDay() - startTime) / 1000000;
            System.out.printf("%s is Top rated show in: %s with execution time: %d%n", topRatedShow, genre, executionTimeInMillis);
        }
    }
}

class TvSeriesResponse {
    public int page;
    public int per_page;
    public Long total;
    public Long total_pages;
    public Data[] data;

    static class Data {
        public String name;
        public String runtime_of_series;
        public String certificate;
        public String runtime_of_episodes;
        public String genre;
        public Double imdb_rating;
        public String overview;
        public Long no_of_votes;
        public Long id;
    }
}