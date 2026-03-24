package com.project;

/**
 * Thrown when required application configuration is messing or invalid.
 * Using an unchecked exception keeps call-sites clean; configuration errors
 * are not recoverable at runtime.
 */
public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
