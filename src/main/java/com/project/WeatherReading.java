package com.project;

import java.time.Instant;

/** Immutable snapshot of a single weather observation from OpenWeatherMap.
 * Temperature is stored in Fahrenheit; humidity is a percentage (0-100).
 */
public final class WeatherReading {

    private final Instant timestamp;
    private final double temperatureF;
    private final double humidity; // percent
    private final String description; // human-readable
    private final double latitude;
    private final double longitude;

    public WeatherReading(Instant timestamp,
                          double temperatureF,
                          double humidity,
                          String description,
                          double latitude,
                          double longitude) {
        this.timestamp = timestamp;
        this.temperatureF = temperatureF;
        this.humidity = humidity;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public double getTemperatureF() {
        return temperatureF;
    }

    public double getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return String.format(
                "WeatherReading{time=%s, temp=%.1fF, humidity=%.1f%%, desc='%s'}",
                timestamp, temperatureF, humidity, description
        );
    }
}
