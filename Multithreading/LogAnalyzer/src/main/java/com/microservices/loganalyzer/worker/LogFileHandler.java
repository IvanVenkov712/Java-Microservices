package com.microservices.loganalyzer.worker;

import com.microservices.loganalyzer.EndpointStatistics;
import com.microservices.loganalyzer.LogData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class LogFileHandler implements Runnable {
    private final Path path;
    private final Map<String, EndpointStatistics> map;

    public LogFileHandler(
        Path path, Map<String, EndpointStatistics> map
    ) {
        this.path = path;
        this.map = map;
    }

    private void processData(LogData newData) {
        map.compute(
            newData.endpoint(),
            (endpoint, statistics) ->
                statistics == null ? EndpointStatistics.initialStatistics(newData) :
                    statistics.getNewStatistics(newData)
        );
    }

    @Override
    public void run() {
        try (var stream = Files.lines(path)) {
            stream.map(LogData::of)
                .forEach(this::processData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
