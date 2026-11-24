package com.microservices.loganalyzer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microservices.loganalyzer.statistics.LogStatisticsCalculator;
import com.microservices.loganalyzer.statistics.LogStatisticsCalculatorImpl;

import java.util.List;

public class Main {
    static void main() {
        LogStatisticsCalculator calculator = new LogStatisticsCalculatorImpl(4);
        List<EndpointStatistics> statistics = calculator.calculateStatistics("logs/");
        Gson gson = new GsonBuilder().create();
        for (EndpointStatistics s : statistics) {
            System.out.println(s);
        }

    }
}
