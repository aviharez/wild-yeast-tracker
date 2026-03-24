package com.project;

/**
 * Thrown by {@link WeatherService} when a weather reading cannot be obtained,
 * whether due to a network error, an HTTP error response, or a parsing failure.
 */
public class WeatherServiceException extends Exception {

    public WeatherServiceException(String message) {
        super(message);
    }

    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
