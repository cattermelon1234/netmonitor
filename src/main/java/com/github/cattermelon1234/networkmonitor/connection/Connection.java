package com.github.cattermelon1234.networkmonitor.connection;

import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

import java.net.Inet4Address;
import java.util.Objects;

public class Connection {
    Inet4Address srcIp;
    Inet4Address dstIp;
    String protocol;

    public Connection(Packet packet) {
        srcIp = packet.get(IpV4Packet.class).getHeader().getSrcAddr();
        dstIp = packet.get(IpV4Packet.class).getHeader().getDstAddr();
        protocol = packet.get(IpV4Packet.class).getHeader().getProtocol().name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return ((srcIp.equals(that.srcIp) && dstIp.equals(that.dstIp)) ||
                (srcIp.equals(that.dstIp) && dstIp.equals(that.srcIp))) && protocol.equals(that.protocol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcIp, dstIp, protocol);
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(srcIp.toString());
        ret.append("-");
        ret.append(dstIp.toString());
        ret.append("-");
        ret.append(protocol);
        return ret.toString();
    }
}
