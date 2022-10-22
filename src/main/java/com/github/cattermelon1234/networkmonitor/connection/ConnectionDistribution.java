package com.github.cattermelon1234.networkmonitor.connection;

import org.pcap4j.packet.Packet;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class ConnectionDistribution {
    public static int INTERVAL = 100;

    HashMap<Connection, Long> currentDist;
    Timestamp startTime;
    int currentTotalCount;

    public ConnectionDistribution() {
        currentTotalCount = 0;
        currentDist = null;
        startTime = null;
    }

    public Double collect(Timestamp timestamp, Packet packet) {
        if (startTime == null) {
            startTime = timestamp;
            currentDist = new HashMap<>();
        }
        long elapsed = timestamp.getTime() - startTime.getTime();
        Connection connection = new Connection(packet);
        currentTotalCount++;
        Long count = currentDist.get(connection);
        if (count == null) {
            currentDist.put(connection, 1L);
        } else {
            currentDist.put(connection, ++count);
        }

        Double entropy = null;
        if (elapsed >= INTERVAL) {
            entropy = calculateEntropy();

            startTime = timestamp;
            currentDist = new HashMap<>();
            currentTotalCount = 0;
        }

        return entropy;
    }

    public double calculateEntropy() {
        double entropy = 0.0;
        for (Map.Entry<Connection, Long> set : currentDist.entrySet()) {
            double p = 1.0 * set.getValue() / currentTotalCount;
            entropy -= p * Math.log(p) / Math.log(2);
        }

        return entropy;
    }

    public String toString() {
        return "";
    }
}
