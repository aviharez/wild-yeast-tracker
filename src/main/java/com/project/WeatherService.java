package com.project;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.Instant;

/**
 * Fetches current weather data from the OpenWeatherMap "Current Weather Data"
 * API (v2.5) and converts the JSON response into a {@link WeatherReading}.
 */
public class WeatherService {

    private static final String API_URL_TEMPLATE =
            "https://api.openweathermap.org/data/2.5/weather" +
            "?lat=%s&lon=%s&units=imperial&appid=%s";

    private final AppConfig config;
    private final HttpClient httpClient;

    public WeatherService(AppConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(config.getConnectTimeoutSeconds()))
                .build();
    }

    /**
     * Fetches the current weather for the coordinates defined in {@link AppConfig}.
     *
     * @return a populated {@link WeatherReading}
     * @throws WeatherServiceException on any network, HTTP, or parsing error
     */
    public WeatherReading fetchCurrentWeather() throws WeatherServiceException {
        String url = buildUrl();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(config.getReadTimeoutSeconds()))
                .GET()
                .build();

        HttpResponse<String> response = sendRequest(request);
        validateHttpStatus(response);
        return parseResponse(response.body());
    }

    // Private helpers

    private String buildUrl() {
        return String.format(API_URL_TEMPLATE,
                config.getLatitude(),
                config.getLongitude(),
                config.getApiKey());
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws WeatherServiceException {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException e) {
            throw new WeatherServiceException(
                    "Request timed out after " + config.getReadTimeoutSeconds() + " seconds. " +
                            "Check your internet connection or increase READ_TIMEOUT_SECONDS in .env.", e
            );
        } catch (IOException e) {
            throw new WeatherServiceException("Network error while contacting OpenWeatherMap: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WeatherServiceException("Request was interrupted.", e);
        }
    }

    private void validateHttpStatus(HttpResponse<String> response) throws WeatherServiceException {
        int status = response.statusCode();
        if (status == 200) return;

        String detail = switch (status) {
            case 401 -> "Invalid API key. Verify OWM_API_KEY in your .env file. " +
                        "New keys can take up to 2 hours to activate after registration.";
            case 404 -> "Location not found. Check MEADERY_LATITUDE ? MEADERY_LONGITUDE in .env.";
            case 429 -> "API rate limit exceeded. Free-tier accounts allow 60 calls/minutes. " +
                        "Wait a moment and try again.";
            case 500, 502, 503 -> "OpenWeatherMap server error (HTTP " + status + "). " +
                                  "This is a temporary issue on their end; try again shortly.";
            default -> "Unexpected HTTP status " + status + ".";
        };

        throw new WeatherServiceException(detail + "\nResponse body: " + response.body());
    }

    private WeatherReading parseResponse(String json) throws WeatherServiceException {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            // Temperature and humidity live under "main"
            JsonObject main = root.getAsJsonObject("main");
            double tempF = main.get("temp").getAsDouble();
            double humidity = main.get("humidity").getAsDouble();

            // Human-readable condition description
            String description = root
                    .getAsJsonArray("weather")
                    .get(0).getAsJsonObject()
                    .get("description").getAsString();

            // Server-side unix timestamp (UTC)
            long epochSeconds = root.get("dt").getAsLong();
            Instant timestamp = Instant.ofEpochSecond(epochSeconds);

            // Coordinates echoed back by the API
            JsonObject coord = root.getAsJsonObject("coord");
            double lat = coord.get("lat").getAsDouble();
            double lon = coord.get("lon").getAsDouble();

            return new WeatherReading(timestamp, tempF, humidity, description, lat, lon);
        } catch (Exception e) {
            throw new WeatherServiceException(
                    "Failed to parse OpenWeatherMap response. " +
                            "The API response format may have changed. \nRaw response: " + json, e
            );
        }
    }

}
