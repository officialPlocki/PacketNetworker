package me.refluxo.networker.packethandler;

import me.refluxo.networker.PacketClient;
import me.refluxo.networker.PacketServer;
import me.refluxo.networker.instance.ConnectionManager;
import me.refluxo.networker.instance.PacketInstance;
import me.refluxo.networker.instance.PacketServiceType;
import me.refluxo.networker.packet.Packet;
import me.refluxo.networker.packet.impl.ConnectPacket;
import me.refluxo.networker.packet.impl.DisconnectPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class PacketDecoder {

    public PacketDecoder(ObjectInputStream input, Socket socket) throws IOException, ClassNotFoundException {
        System.out.println("Got Packet");
        PacketHandler handler = null;
        String sessionID = input.readUTF();
        boolean b1 = false;
        if(PacketInstance.getInstance().equals(PacketServiceType.CLIENT)) {
            handler = PacketClient.inst.getPacketHandler();
            if(sessionID.equals(PacketClient.inst.getSessionID())) {
                b1 = true;
            }
        } else if(PacketInstance.getInstance().equals(PacketServiceType.SERVER)) {
            handler = PacketServer.inst.getPacketHandler();
            if(sessionID.equals(PacketServer.inst.getSessionID())) {
                b1 = true;
            }
        }
        String conName = input.readUTF();
        if(b1) {
            int packetID = input.readInt();
            Class<? extends Packet> packet = handler.getPackets().get(packetID);
            System.out.println(packet.getName());
            if(PacketInstance.getInstance().equals(PacketServiceType.SERVER)) {
                if(packet.equals(DisconnectPacket.class)) {
                    new ConnectionManager().removeConnection(conName);
                }
            }
            System.out.println("Packet validated.");
            try {
                packet.newInstance().read(input);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if(PacketInstance.getInstance().equals(PacketServiceType.SERVER)) {
                if(packet.equals(ConnectPacket.class)) {
                    new ConnectionManager().addConnection(conName, socket);
                }
            }
        }
    }

}
