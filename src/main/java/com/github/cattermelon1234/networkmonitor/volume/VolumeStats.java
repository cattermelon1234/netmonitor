package com.github.cattermelon1234.networkmonitor.volume;

import java.net.Inet4Address;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class VolumeStats {
    public static int INTERNAL = 100; //10msec
    Timestamp startTime;
    long count;

    TreeMap<Timestamp, Long> volumeStats;

    public VolumeStats() {
        startTime = null;
        count = 0;
        volumeStats = new TreeMap<Timestamp, Long>();
    }

    public void collect(Timestamp timestamp) {
        if (startTime == null) {
            startTime = timestamp;
        }

        long elapsed = timestamp.getTime() - startTime.getTime();
        if (elapsed > INTERNAL) {
            volumeStats.put(startTime, ++count);
            count = 0;
            elapsed = 0;
            startTime = timestamp;
        } else {
            count++;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        Iterator<Map.Entry<Timestamp, Long>> it = volumeStats.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Timestamp, Long> entry = it.next();
            String line = entry.getKey().toString() + ": " + entry.getValue() + "\n";
            result = result.append(line);
        }

        return result.toString();
    }}
