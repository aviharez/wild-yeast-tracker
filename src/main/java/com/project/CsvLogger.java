package com.project;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Appends weather readings and their catchment status to a local CSV file.
 *
 * <p>The header row is written automatically on the first run (when the file
 * does not yet exist or is empty). On subsequent runs the file is opened in
 * append mode so no existing data is overwritten.
 */
public class CsvLogger {

    private static final String HEADER = "timestamp_utc,latitude,longitude,temperature_f,humidity_pct,conditions,catchment_status";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC"));

    private final Path logPath;

    public CsvLogger(AppConfig config) {
        this.logPath = Path.of(config.getLogFile());
    }

    /**
     * Appends one row to the CSV file for the given reading and status.
     *
     * @throws IOException if the file cannot be written
     */
    public void log(WeatherReading reading, CatchmentStatus status) throws IOException {
        boolean needsHeader = !Files.exists(logPath) || Files.size(logPath) == 0;

        try (PrintWriter writer = new PrintWriter(new FileWriter(logPath.toFile(), true))) {
            if (needsHeader) {
                writer.println(HEADER);
            }
            writer.println(buildRow(reading, status));
        }
    }

    /**
     * Returns the absolute path of the CSV file for display purposes.
     */
    public String getAbsolutePath() {
        return logPath.toAbsolutePath().toString();
    }

    // private helper

    private String buildRow(WeatherReading reading, CatchmentStatus status) {
        return String.join(",",
                FORMATTER.format(reading.getTimestamp()),
                formatDecimal(reading.getLatitude()),
                formatDecimal(reading.getLongitude()),
                formatDecimal(reading.getTemperatureF()),
                formatDecimal(reading.getHumidity()),
                escapeCsv(reading.getDescription()),
                escapeCsv(status.label()));
    }

    private String formatDecimal(double value) {
        return String.format("%.4f", value);
    }

    /**
     * Wraps a field in double-quotes if it contains a comma, double-quote,
     * or newline. Minimal RFC 4180-compliant escaping.
     */
    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

}
