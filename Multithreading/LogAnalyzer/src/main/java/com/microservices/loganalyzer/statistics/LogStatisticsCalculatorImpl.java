package com.microservices.loganalyzer.statistics;

import com.microservices.loganalyzer.EndpointStatistics;
import com.microservices.loganalyzer.worker.LogFileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogStatisticsCalculatorImpl implements LogStatisticsCalculator {
    private final int nThreads;

    public LogStatisticsCalculatorImpl(int nThreads) {
        if (nThreads <= 0) {
            throw new IllegalArgumentException("nThreads must be positive");
        }
        this.nThreads = nThreads;
    }


    @Override
    public List<EndpointStatistics> calculateStatistics(String logFilesPath) {
        Map<String, EndpointStatistics> map = new ConcurrentHashMap<>();
        Path dir = Path.of(logFilesPath);
        try (ExecutorService pool = Executors.newFixedThreadPool(nThreads)) {
            try (var logFiles = Files.newDirectoryStream(dir, "*.log")) {
                for (Path path : logFiles) {
                    pool.execute(new LogFileHandler(path, map));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return map.values().stream().toList();
    }
}
