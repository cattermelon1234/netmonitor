package com.github.cattermelon1234.networkmonitor.rule;

public class ConnectionDistributionRule {
    public double mean;
    public double threshold;

    public ConnectionDistributionRule(double mean, double threshold) {
        this.mean = mean;
        this.threshold = threshold;
    }

    public boolean validate(Double value) {
        if (value == null || value == 0.0)
            return true;

        double deviation = value - mean;
        if (deviation < threshold)
            return true;
        else
            return false;
    }

    public String toString() {
        return "ConnectionDistributionRule: mean(" + mean + ") threshold(" + threshold + ")";
    }
}
