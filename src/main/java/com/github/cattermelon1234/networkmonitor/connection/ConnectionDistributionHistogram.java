package com.github.cattermelon1234.networkmonitor.connection;

import com.github.cattermelon1234.networkmonitor.connection.Connection;
import org.pcap4j.packet.Packet;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class ConnectionDistributionHistogram {
    public static int INTERVAL = 100;

    HashMap<Connection, Long> currentDist;
    TreeMap<Timestamp, Double> distributionOverTime;
    Timestamp startTime;
    int currentTotalCount;

    public ConnectionDistributionHistogram() {
        currentTotalCount = 0;
        currentDist = null;
        startTime = null;
        distributionOverTime = new TreeMap<>();
    }

    public void collect(Timestamp timestamp, Packet packet) {
        if (startTime == null) {
            startTime = timestamp;
            currentDist = new HashMap<>();
        }
        long elapsed = timestamp.getTime() - startTime.getTime();
        Connection connection = new Connection(packet);
        if (elapsed > INTERVAL) {
            double entropy = calculateEntropy();
            distributionOverTime.put(startTime, entropy);

            startTime = timestamp;
            currentDist = new HashMap<>();
            currentTotalCount = 1;
        } else {
            currentTotalCount++;
            Long count = currentDist.get(connection);
            if (count == null) {
                currentDist.put(connection, 1L);
            } else {
                currentDist.put(connection, ++count);
            }
        }
    }

    public double calculateEntropy() {
        double entropy = 0.0;
        for (Map.Entry<Connection, Long> set : currentDist.entrySet()) {
            double p = 1.0 * set.getValue() / currentTotalCount;
            entropy -= p * Math.log(p) / Math.log(2);
        }

        return entropy;
    }

    public double calculateMean() {
        double sum = 0.0;
        Iterator<Map.Entry<Timestamp, Double>> it = distributionOverTime.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Timestamp, Double> entry = it.next();
            sum = sum + entry.getValue();
        }

        return sum/distributionOverTime.size();
    }

    public double getMaxDeviationFromMean(double mean) {
        double threshold = 0.0;
        Iterator<Map.Entry<Timestamp, Double>> it = distributionOverTime.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Timestamp, Double> entry = it.next();
            double deviation = Math.abs(entry.getValue() - mean);
            if (deviation > threshold)
                threshold = deviation;
        }

        return threshold;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        Iterator<Map.Entry<Timestamp, Double>> it = distributionOverTime.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Timestamp, Double> entry = it.next();
            String line = entry.getKey().toString() + ": " + entry.getValue() + "\n";
            result = result.append(line);
        }

        return result.toString();
    }
}
