package me.refluxo.networker.packethandler;

import me.refluxo.networker.packet.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketHandler {

    private final List<Class<? extends Packet>> packets;

    public PacketHandler() {
        packets = new ArrayList<>();
    }

    public void registerPacket(Packet packet) {
        packets.add(packet.getClass());
    }

    public void registerPacket(Class<? extends Packet> packet) {
        packets.add(packet);
    }

    public int getPacketID(Packet packet) {
        AtomicInteger i = new AtomicInteger(-1);
        AtomicInteger t = new AtomicInteger(0);
        getPackets().forEach(obj -> {
            if(obj.equals(packet.getClass())) {
                i.set(t.get());
            }
            t.set(t.get()+1);
        });
        return i.get();
    }

    public boolean packetIsRegistered(Class<? extends Packet> clazz) {
        return packets.contains(clazz);
    }

    public List<Class<? extends Packet>> getPackets() {
        return packets;
    }

    public void unRegisterPacket(Packet packet) {
        packets.remove(packet.getClass());
    }

}
