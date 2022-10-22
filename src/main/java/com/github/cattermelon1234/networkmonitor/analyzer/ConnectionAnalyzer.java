package com.github.cattermelon1234.networkmonitor.analyzer;

import com.github.cattermelon1234.networkmonitor.alert.Alert;
import com.github.cattermelon1234.networkmonitor.connection.ConnectionDistribution;
import com.github.cattermelon1234.networkmonitor.connection.ConnectionDistributionHistogram;
import com.github.cattermelon1234.networkmonitor.rule.ConnectionDistributionRule;
import org.pcap4j.packet.Packet;

import java.sql.Timestamp;

public class ConnectionAnalyzer {
    public enum State {
        LEARN,
        DETECTION,
        ALERT
    }
    public static long TIME_TO_LEARN = 120000; // 2 minutes
    public static long TIME_TO_CLEAR_ALERT = 300000; // 5 minutes
    public static int THRESHOLD_BUFFER = 50; // Add 20% to threshold

    ConnectionDistribution connectionDistribution;
    ConnectionDistributionHistogram connectionDistributionHistogram;
    ConnectionDistributionRule rule;
    State currentState;

    Timestamp startTime;
    Timestamp clearTime;

    Alert alert;

    public ConnectionAnalyzer(Alert alert) {
        connectionDistribution = new ConnectionDistribution();
        connectionDistributionHistogram = new ConnectionDistributionHistogram();
        currentState = State.LEARN;
        this.alert = alert;

        startTime = null;
        clearTime = null;
        rule = null;
    }

    public void analyze(Timestamp timestamp, Packet packet) {
        long elapsed;
        if (startTime == null) {
            startTime = timestamp;
            System.out.println(timestamp.toString() + ":Start monitoring as " + currentState.toString());
        }
        elapsed = timestamp.getTime() - startTime.getTime();
        if (currentState == State.LEARN) {
            connectionDistributionHistogram.collect(timestamp, packet);
            if (elapsed >= TIME_TO_LEARN) {
                calculateRule();
                currentState = State.DETECTION;
                System.out.println(timestamp.toString() + ":Completed learning. Now move to detection state. Rule: " + rule.toString());
            }
        } else if (currentState == State.DETECTION) {
            Double entropy = connectionDistribution.collect(timestamp, packet);
            if (!rule.validate(entropy)) {
                System.out.println(timestamp.toString() + ":Alert happened at entropy value " + entropy);
                currentState = State.ALERT;
                alert.alert("Anomalous traffic detected at entropy value " + entropy);
            }
        } else if (currentState == State.ALERT) {
            Double entropy = connectionDistribution.collect(timestamp, packet);
            if (rule.validate(entropy)) {
                // Normal traffic
                if (clearTime == null) {
                    clearTime = timestamp;
                } else {
                    long elapsedSinceCleared = timestamp.getTime() - clearTime.getTime();
                    if (elapsedSinceCleared >= TIME_TO_CLEAR_ALERT) {
                        System.out.println(timestamp.toString() + ":Alert cleared");
                        alert.clearAlert("Traffic back to normal at entropy value " + entropy);
                        currentState = State.DETECTION;
                        clearTime = null;
                    }
                }
            } else {
                clearTime = null;
            }
        }

    }

    public void calculateRule() {
        double mean = connectionDistributionHistogram.calculateMean();
        double threshold = connectionDistributionHistogram.getMaxDeviationFromMean(mean);
        threshold = threshold * (100 + THRESHOLD_BUFFER)/100;
        rule = new ConnectionDistributionRule(mean, threshold);
    }
}
