package com.project;

/**
 * Evaluates a {@link WeatherReading} against the meadery's catchment thresholds
 * and assigns a {@link CatchmentStatus}.
 */
public class CatchmentEvaluator {

    private final double tempMinF;
    private final double tempMaxF;
    private final double humidityMin;

    public CatchmentEvaluator(AppConfig config) {
        this.tempMinF = config.getTempMinF();
        this.tempMaxF = config.getTempMaxF();
        this.humidityMin = config.getHumidityMin();
    }

    /**
     * Evaluates the supplied reading and returns the appropriate status.
     */
    public CatchmentStatus evaluate(WeatherReading reading) {
        boolean tempOk = isTempInRange(reading.getTemperatureF());
        boolean humidityOk = isHumidityInRange(reading.getHumidity());

        if (tempOk && humidityOk) return CatchmentStatus.OPTIMAL;
        if (tempOk || humidityOk) return CatchmentStatus.SUB_OPTIMAL;
        return CatchmentStatus.CLOSED;
    }

    /**
     * Returns a human-readable explanation of why the status was assigned.
     * Useful for console output and debugging.
     */
    public String explain(WeatherReading reading, CatchmentStatus status) {
        boolean tempOk = isTempInRange(reading.getTemperatureF());
        boolean humidityOk = isHumidityInRange(reading.getHumidity());

        String tempResult = tempOk
                ? String.format("temp %.1fF is within [%.0f-%.0fF]", reading.getTemperatureF(), tempMinF, tempMaxF)
                : String.format("temp %.1fF is outside [%.0f-%.0fF]", reading.getTemperatureF(), tempMinF, tempMaxF);

        String humResult = humidityOk
                ? String.format("humidity %.0f%% meets minimum %.0f%%", reading.getHumidity(), humidityMin)
                : String.format("humidity %.0f%% is below minimum %.0f%%", reading.getHumidity(), humidityMin);

        return String.format("Status: %s (%s; %s)", status.label(), tempResult, humResult);
    }

    // private helper

    private boolean isTempInRange(double tempF) {
        return tempF >= tempMinF && tempF <= tempMaxF;
    }

    private boolean isHumidityInRange(double humidity) {
        return humidity >= humidityMin;
    }

}
