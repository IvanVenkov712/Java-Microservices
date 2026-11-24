package com.microservices.loganalyzer;

public record EndpointStatistics (
    String endpoint,
    int totalQueriesCount,
    int failedQueriesCount,
    double averageExecutionTime,
    int minExecutionTime,
    int maxExecutionTime
) {
    public double getNewAverage(double newElem) {
        double oldAvgCoef = (double)totalQueriesCount / (totalQueriesCount + 1);
        double newElemCoef = 1.0 / (totalQueriesCount + 1);
        return averageExecutionTime * oldAvgCoef + newElem * newElemCoef;
    }

    public EndpointStatistics getNewStatistics(LogData newData) {
        if (!endpoint.equals(newData.endpoint())) {
            throw new IllegalArgumentException("Different endpoints");
        }
        int newTotalQueriesCount = totalQueriesCount + 1;
        int newFailedQueriesCount = failedQueriesCount;
        if (newData.status() >= 500 && newData.status() < 600) {
            newFailedQueriesCount++;
        }
        double newAverage = getNewAverage(newData.executionTimeMs());
        int newMin = Math.min(minExecutionTime, newData.executionTimeMs());
        int newMax = Math.max(maxExecutionTime, newData.executionTimeMs());

        return new EndpointStatistics(
            endpoint, newTotalQueriesCount, newFailedQueriesCount, newAverage, newMin, newMax
        );
    }

    public static EndpointStatistics emptyStatistics(String endpoint) {
        return new EndpointStatistics (
            endpoint, 0, 0, 0,
            Integer.MAX_VALUE, Integer.MIN_VALUE
        );
    }

    public static EndpointStatistics initialStatistics(LogData data) {
        return emptyStatistics(data.endpoint()).getNewStatistics(data);
    }

    @Override
    public String toString() {
        return
            endpoint() + "\n" +
            "\tRequests: " + totalQueriesCount() + "\n" +
            "\tFailures: " + failedQueriesCount() + "\n" +
            "\tAvg Latency: " + averageExecutionTime() + "\n" +
            "\tMin Latency: " + minExecutionTime() + "\n" +
            "\tMax Latency: " + maxExecutionTime() + "\n";
    }
}
