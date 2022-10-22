package com.github.cattermelon1234.networkmonitor.monitor;

import com.github.cattermelon1234.networkmonitor.alert.*;
import com.github.cattermelon1234.networkmonitor.analyzer.ConnectionAnalyzer;
import com.github.cattermelon1234.networkmonitor.connection.ConnectionDistributionHistogram;
import com.github.cattermelon1234.networkmonitor.volume.VolumeStats;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

public class PcapMonitor {
    public void analyze(String inputFile, Alert alert) {
        PcapHandle handle = null;
        try {
            //
            handle = Pcaps.openOffline(inputFile, PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException e) {
            e.printStackTrace();
        }

        if (handle == null) {
            System.out.println("Failed to read the pcap file " + inputFile);
            System.exit(1);
        }

        ConnectionAnalyzer connectionAnalyzer = new ConnectionAnalyzer(alert);
        //VolumeStats volumeStats = new VolumeStats();
        while (true) {
            try {
                Packet packet = handle.getNextPacketEx();
                if ((packet.contains(TcpPacket.class) || packet.contains(UdpPacket.class)) &&
                        packet.contains(IpV4Packet.class)) {
                    //volumeStats.collect(handle.getTimestamp());
                    connectionAnalyzer.analyze(handle.getTimestamp(), packet);
                    //System.out.println(handle.getTimestamp());
                } else {
                    //System.out.println("Skip packet");
                }
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (PcapNativeException e) {
                e.printStackTrace();
            } catch (NotOpenException e) {
                e.printStackTrace();
            } catch (EOFException e) {
                System.out.println("EOF");
                break;
            }
        }

        //System.out.println("Result of volume statistics:");
        //System.out.println(volumeStats.toString());

        //System.out.println("Result of IPV4 Address distribution:");
        //System.out.println(connectionDistribution.toString());

        handle.close();
    }
}
