package com.microservices.loganalyzer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public record LogData(
    LocalDateTime time,
    String method,
    String endpoint,
    int status,
    int executionTimeMs
) {
    private static final Set<String> METHODS = Set.of(
        "GET", "POST", "PUT", "DELETE", "HEAD", "PATCH", "OPTIONS"
    );

    public LogData {
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("Method cannot be null");
        }
        if(!METHODS.contains(method)) {
            throw new IllegalArgumentException("Not a valid HTTP method");
        }
        if (endpoint == null || endpoint.isEmpty()) {
            throw new IllegalArgumentException("Empty endpoint is not allowed");
        }
        if (!(status >= 100 && status < 600)) {
            throw new IllegalArgumentException("Not a valid HTTP status code");
        }
        if (executionTimeMs < 0) {
            throw new IllegalArgumentException("Execution time must be positive");
        }
    }

    public static LogData of(String line) {
        String[] tokens = line.strip().split("\\s+");
        if (tokens.length != 5) {
            throw new IllegalArgumentException("Invalid line format");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime time = LocalDateTime.parse(tokens[0], formatter);
        String method = tokens[1];
        String endpoint = tokens[2];
        int status = Integer.parseInt(tokens[3]);
        int executionTimeMs = Integer.parseInt(tokens[4]);
        return new LogData (
            time, method, endpoint, status, executionTimeMs
        );
    }
}
