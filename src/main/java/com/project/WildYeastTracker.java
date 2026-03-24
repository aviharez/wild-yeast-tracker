package com.project;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class WildYeastTracker {

    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("EEE, MMM d yyyy HH:mm z");

    public static void main(String[] args) {
        printBanner();

        // configuration
        AppConfig config;
        try {
            config = new AppConfig();
        } catch (ConfigurationException e) {
            System.err.println("[ERROR] Configuration problem:");
            System.err.println("  " + e.getMessage());
            System.exit(1);
            return;
        }

        // Fetch weather
        WeatherService weatherService = new WeatherService(config);
        CatchmentEvaluator evaluator = new CatchmentEvaluator(config);
        CsvLogger logger = new CsvLogger(config);

        WeatherReading reading;
        try {
            System.out.printf("Fetching weather for coordinates (%.4f, %.4f)...%n", config.getLatitude(), config.getLongitude());
            reading = weatherService.fetchCurrentWeather();
        } catch (WeatherServiceException e) {
            System.err.println("[ERROR] Could not retrieve weather data:");
            System.err.println("  " + e.getMessage());
            System.exit(2);
            return;
        }

        // Evaluate
        CatchmentStatus status = evaluator.evaluate(reading);
        String explanation = evaluator.explain(reading, status);

        // Log to CSV
        try {
            logger.log(reading, status);
        } catch (IOException e) {
            System.err.println("[ERROR] Could not write to CSV log:");
            System.err.println("  " + e.getMessage());
            System.err.println("  Check that the application has write permission to : " + logger.getAbsolutePath());
            System.exit(3);
            return;
        }

        // print summary
        printSummary(reading, status, explanation, logger.getAbsolutePath());
    }

    private static void printBanner() {
        System.out.println("=".repeat(40));
        System.out.println("  Copper & Moss - Wild Yeast Tracker");
        System.out.println("=".repeat(40));
        String now = ZonedDateTime.now(ZoneId.systemDefault()).format(DISPLAY_FMT);
        System.out.println("  Run time : " + now);
        System.out.println("-".repeat(40));
    }

    private static void printSummary(WeatherReading reading,
                                     CatchmentStatus status,
                                     String explanation,
                                     String csvPath) {
        System.out.println();
        System.out.printf("  Conditions  : %s%n", capitalize(reading.getDescription()));
        System.out.printf("  Temperature : %.1fF%n", reading.getTemperatureF());
        System.out.printf("  Humidity    : %.0f%%%n", reading.getHumidity());
        System.out.println();
        System.out.println("  " + explanation);
        System.out.println();

        String badge = switch (status) {
            case OPTIMAL -> "[ OPTIMAL ] Open the honey-must - wild yeast is waiting!";
            case SUB_OPTIMAL -> "[ SUB-OPTIMAL ] Conditions are marginal; proceed with caution.";
            case CLOSED -> "[ CLOSED ] Conditions are not suitable for catchment";
        };
        System.out.println("  " + badge);
        System.out.println();
        System.out.println("  Logged to: " + csvPath);
        System.out.println("=".repeat(40));
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

}
