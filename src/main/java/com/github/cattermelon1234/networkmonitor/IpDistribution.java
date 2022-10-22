package com.github.cattermelon1234.networkmonitor;

import org.pcap4j.core.PcapIpV4Address;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

import java.net.Inet4Address;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class IpDistribution {
    public static int INTERVAL = 100;

    HashMap<Inet4Address, Long> currentDist;
    HashMap<Timestamp, Double> ipDistributionOverTime;
    Timestamp startTime;

    public IpDistribution() {
        currentDist = null;
        startTime = null;
        ipDistributionOverTime = new HashMap<>();
    }

    public void collect(Timestamp timestamp, Packet packet) {
        if (startTime == null) {
            startTime = timestamp;
            currentDist = new HashMap<Inet4Address, Long>();
        }
        long elapsed = timestamp.getTime() - startTime.getTime();
        Inet4Address srcAddr = packet.get(IpV4Packet.class).getHeader().getSrcAddr();
        if (elapsed > INTERVAL) {
            double entropy = calculateEntropy();
            ipDistributionOverTime.put(startTime, entropy);

            startTime = timestamp;
            currentDist = new HashMap<Inet4Address, Long>();
        } else {
            Long count = currentDist.get(srcAddr);
            if (count == null) {
                currentDist.put(srcAddr, 1L);
            } else {
                currentDist.put(srcAddr, ++count);
            }
        }
    }

    public double calculateEntropy() {
        double entropy = 0.0;
        int totalCount = currentDist.size();
        for (Map.Entry<Inet4Address, Long> set : currentDist.entrySet()) {
            double p = 1.0 * set.getValue() / totalCount;
            entropy -= p * Math.log(p) / Math.log(2);
        }

        return entropy;
    }
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Timestamp, Double> set : ipDistributionOverTime.entrySet()) {
            String line = set.getKey().toString() + ": " + set.getValue() + "\n";
            result = result.append(line);
        }

        return result.toString();
    }
}
