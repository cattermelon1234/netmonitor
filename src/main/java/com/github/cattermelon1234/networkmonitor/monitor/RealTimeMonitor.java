package com.github.cattermelon1234.networkmonitor.monitor;

import com.github.cattermelon1234.networkmonitor.IpDistribution;
import com.github.cattermelon1234.networkmonitor.alert.Alert;
import com.github.cattermelon1234.networkmonitor.analyzer.ConnectionAnalyzer;
import com.github.cattermelon1234.networkmonitor.volume.VolumeStats;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.util.NifSelector;

import java.io.IOException;

public class RealTimeMonitor {
    public void analyze(Alert alert) {
        // The class that will store the network device
        // we want to use for capturing.
        PcapNetworkInterface device = null;

        // Pcap4j comes with a convenient method for listing
        // and choosing a network interface from the terminal
        try {
            // List the network devices available with a prompt
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("You chose: " + device);


        // New code below here
        if (device == null) {
            System.out.println("No device chosen.");
            System.exit(1);
        }

        // Open the device and get a handle
        int snapshotLength = 65536; // in bytes
        int readTimeout = 50; // in milliseconds

        ConnectionAnalyzer connectionAnalyzer = new ConnectionAnalyzer(alert);
        //VolumeStats volumeStats = new VolumeStats();
        try {
            final PcapHandle handle;
            handle = device.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);

            // Create a listener that defines what to do with the received packets
            PacketListener listener = new PacketListener() {
                @Override
                public void gotPacket(Packet packet) {
                    // Override the default gotPacket() function and process packet
                    //System.out.println(handle.getTimestamp() + ": Received packet");
                    //System.out.println(packet);
                    if ((packet.contains(TcpPacket.class) || packet.contains(UdpPacket.class)) &&
                            packet.contains(IpV4Packet.class)) {
                        connectionAnalyzer.analyze(handle.getTimestamp(), packet);

                        //volumeStats.collect(handle.getTimestamp());
                        //ipDistribution.collect(handle.getTimestamp(), packet);
                        //System.out.println(handle.getTimestamp());
                    } else {
                        System.out.println("Skip packet");
                    }
                }
            };

            // Tell the handle to loop using the listener we created
            try {
                int maxPackets = 5000;
                handle.loop(maxPackets, listener);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (PcapNativeException e) {
                e.printStackTrace();
            } catch (NotOpenException e) {
                e.printStackTrace();
            }

            //System.out.println("Result of volume statistics:");
            //System.out.println(volumeStats.toString());

            //System.out.println("Result of IPV4 Address distribution:");
            //System.out.println(ipDistribution.toString());

            // Cleanup when complete
            handle.close();
        } catch (PcapNativeException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
