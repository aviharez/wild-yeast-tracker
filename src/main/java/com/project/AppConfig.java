package com.project;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

/**
 * Loads and validates all application configuration from environment variables
 * or a .env file. Fails fast with a clear message if required values are missing.
 */
public class AppConfig {

    // Meadery location
    // Replace these with the actual coordinates of Copper & Moss Meadery.
    private static final double DEFAULT_LATITUDE = -6.175; // Jakarta, Indonesia (placeholder)
    private static final double DEFAULT_LONGITUDE = 106.827; // Jakarta, Indonesia (placeholder)

    // Catchment thresholds
    private static final double DEFAULT_TEMP_MIN_F = 60.0;
    private static final double DEFAULT_TEMP_MAX_F = 75.0;
    private static final double DEFAULT_HUMIDITY_MIN = 50.0;

    // HTTP
    private static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 10;
    private static final int DEFAULT_READ_TIMEOUT_SECONDS = 10;

    // CSV output
    private static final String DEFAULT_LOG_FILE = "yeast_log.csv";

    private final String apiKey;
    private final double latitude;
    private final double longitude;
    private final double tempMinF;
    private final double tempMaxF;
    private final double humidityMin;
    private final int connectTimeoutSeconds;
    private final int readTimeoutSeconds;
    private final String logFile;

    public AppConfig() {
        Dotenv dotenv = loadDotenv();

        this.apiKey = requireEnv(dotenv, "OWM_API_KEY", "Your OpenWeatherMap API key. Get a free one at https://openweathermap.org/api");

        this.latitude = parseDouble(dotenv, "MEADERY_LATITUDE", DEFAULT_LATITUDE);
        this.longitude = parseDouble(dotenv, "MEADERY_LONGITUDE", DEFAULT_LONGITUDE);

        this.tempMinF = parseDouble(dotenv, "TEMP_MIN_F", DEFAULT_TEMP_MIN_F);
        this.tempMaxF = parseDouble(dotenv, "TEMP_MAX_F", DEFAULT_TEMP_MAX_F);
        this.humidityMin = parseDouble(dotenv, "HUMIDITY_MIN", DEFAULT_HUMIDITY_MIN);

        this.connectTimeoutSeconds = parseInt(dotenv, "CONNECT_TIMEOUT_SECONDS", DEFAULT_CONNECT_TIMEOUT_SECONDS);
        this.readTimeoutSeconds = parseInt(dotenv, "READ_TIMEOUT_SECONDS", DEFAULT_READ_TIMEOUT_SECONDS);

        this.logFile = getOrDefault(dotenv, "LOG_FILE", DEFAULT_LOG_FILE);
    }

    // Getters

    public String getApiKey() {
        return apiKey;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getTempMinF() {
        return tempMinF;
    }

    public double getTempMaxF() {
        return tempMaxF;
    }

    public double getHumidityMin() {
        return humidityMin;
    }

    public int getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    public String getLogFile() {
        return logFile;
    }

    // Private helpers

    private Dotenv loadDotenv() {
        try {
            return Dotenv.configure().ignoreIfMissing().load();
        } catch (DotenvException e) {
            // .env file exists but is malformed' surface the error clearly.
            throw new ConfigurationException("Failed to parse .env file: " + e.getMessage(), e);
        }
    }

    private String requireEnv(Dotenv dotenv, String key, String hint) {
        String value = dotenv.get(key);
        if (value == null || value.isBlank()) {
            // fallback to real system env (dotenv-java usually merges these, but be explicit for clarity).
            value = System.getenv(key);
        }
        if (value == null || value.isBlank()) {
            throw new ConfigurationException(
                    "Required environment variable '" + key + "' is not set.\n" +
                            "Hint: " + hint + "\n" +
                            "Add it to your .env file or export it in your shell before running."
            );
        }
        return value.trim();
    }

    private String getOrDefault(Dotenv dotenv, String key, String defaultValue) {
        String value = dotenv.get(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }

    private double parseDouble(Dotenv dotenv, String key, double defaultValue) {
        String raw = getOrDefault(dotenv, key, null);
        if (raw == null) return defaultValue;

        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Environment variable '" + key + "' must be a number, got: '" + raw + "'");
        }
    }

    private int parseInt(Dotenv dotenv, String key, int defaultValue) {
        String raw = getOrDefault(dotenv, key, null);
        if (raw == null) return defaultValue;
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Environment variable '" + key + "' must be an integer, got: '" + raw + "'");
        }
    }

}
