package com.github.cattermelon1234.networkmonitor.alert;

public interface Alert {
    public void alert(String text);
    public void clearAlert(String text);
}
