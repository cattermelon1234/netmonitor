package com.github.cattermelon1234.networkmonitor;

import com.github.cattermelon1234.networkmonitor.alert.Alert;
import com.github.cattermelon1234.networkmonitor.alert.EmailAlert;
import com.github.cattermelon1234.networkmonitor.alert.PrintAlert;
import com.github.cattermelon1234.networkmonitor.monitor.PcapMonitor;
import com.github.cattermelon1234.networkmonitor.monitor.RealTimeMonitor;
import org.apache.commons.cli.*;

public class NetworkMonitor {
    public static String DETECTION_MODE = "detect";
    public static String LEARNING_MODE = "learn";

    public static void printUsage(String message, Options options) {
        HelpFormatter formatter = new HelpFormatter();

        System.out.println(message);
        formatter.printHelp("netmonitor learn|detect", options);
    }

    public static void main(String[] args) {
        /*
         * usage: netmonitor learn|detect
         *  -f,--file <arg>      Pcap file path
         *  -h,--host <arg>      Email SMTP host
         *  -s,--sender <arg>    Sender email
         *  -r,--recipient <arg> Recipient email
         *
         *  Example:
         * The following command monitor the traffic from the given
         * pcap file and alert by printing an error message to the console
         *
         * netmonitor -f /opt/myfile.pcap
         *
         * The following command monitor the traffic from the given
         * pcap file and alert by sending an email with the following:
         * smtp host: 1.0.0.127
         * from: sender@gmail.com
         * to: recipient@gmail.com
         *
         * netmonitor -f /opt/myfile.pcap -h 1.0.0.127 -s sender@gmail.com -r recipient@gmail.com
         */
        Options options = new Options();

        Option inputFileOption = new Option("f", "file", true, "Pcap file path");
        inputFileOption.setRequired(false);
        options.addOption(inputFileOption);

        Option smtpHost = new Option("h", "host", true, "SMTP host");
        smtpHost.setRequired(false);
        options.addOption(smtpHost);

        Option sender = new Option("s", "sender", true, "Sender email");
        sender.setRequired(false);
        options.addOption(sender);

        Option recipient = new Option("s", "sender", true, "Recipient email");
        recipient.setRequired(false);
        options.addOption(recipient);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose

        /*
        if (args.length <= 1) {
            printUsage("Missing required option", options);
            System.exit(1);
        }

        boolean isDetection = true;
        if (args[0].equals(DETECTION_MODE)) {
            isDetection = true;
        } else if (args[0].equals(LEARNING_MODE)) {
            isDetection = false;
        } else {
            printUsage("Please specify the monitoring mode. (detect or learn)", options);
            System.exit(1);
        }

        String[] remainingArgs = new String[args.length-1];
        for (int i=0; i<(args.length-1); i++) {
            remainingArgs[i] = args[i+1];
        }
         */

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printUsage(e.getMessage(), options);
            System.exit(1);
        }

        String inputFile = null;
        boolean isRealTime = true;
        if (cmd.hasOption("f")) {
            // <HOME>/Downloads/HTTPDoSNovember2021.pcapng
            inputFile = cmd.getOptionValue("f");
            isRealTime = false;
        }

        String smtpHostStr = null;
        String senderStr = null;
        String recipientStr = null;
        if (cmd.hasOption("h")) {
            smtpHostStr = cmd.getOptionValue("h");
        }
        if (cmd.hasOption("s")) {
            senderStr = cmd.getOptionValue("s");
        }
        if (cmd.hasOption("r")) {
            recipientStr = cmd.getOptionValue("r");
        }

        Alert alert;
        if (smtpHostStr != null && senderStr != null && recipientStr != null) {
            alert = new EmailAlert(smtpHostStr, senderStr, recipientStr);
        } else {
            alert = new PrintAlert();
        }

        if (isRealTime) {
            (new RealTimeMonitor()).analyze(alert);
        } else {
            (new PcapMonitor()).analyze(inputFile, alert);
        }
    }
}
