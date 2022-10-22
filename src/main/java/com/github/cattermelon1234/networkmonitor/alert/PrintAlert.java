package com.github.cattermelon1234.networkmonitor.alert;

public class PrintAlert implements Alert {
    public void alert(String text) {
        System.out.println("!!!Alert!!!");
        System.out.println(text);
    }

    public void clearAlert(String text) {
        System.out.println("!!!Alert Cleared!!!");
        System.out.println(text);
    }
}
