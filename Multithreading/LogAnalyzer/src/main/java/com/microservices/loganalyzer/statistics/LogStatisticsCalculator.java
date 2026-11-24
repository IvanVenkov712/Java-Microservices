package com.microservices.loganalyzer.statistics;

import com.microservices.loganalyzer.EndpointStatistics;

import java.util.List;

public interface LogStatisticsCalculator {
    List<EndpointStatistics> calculateStatistics(String logFilesPath);
}
