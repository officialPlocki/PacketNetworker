package me.refluxo.networker;

import me.refluxo.networker.instance.PacketInstance;
import me.refluxo.networker.instance.PacketServiceType;
import me.refluxo.networker.packet.Packet;
import me.refluxo.networker.packet.impl.ConnectPacket;
import me.refluxo.networker.packet.impl.DisconnectPacket;
import me.refluxo.networker.packethandler.PacketDecoder;
import me.refluxo.networker.packethandler.PacketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PacketClient {

    private PacketHandler packetHandler;
    private String session;
    private String connectionName;
    private Socket socket;
    public static PacketClient inst;

    public PacketClient() {
        inst = this;
        PacketInstance.setInstance(PacketServiceType.CLIENT);
    }

    public void start(String session, String connectionName, String host, int port) {
        this.session = session;
        this.connectionName = connectionName;
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.socket = socket;
        System.out.println("Client opened Socket");
        packetHandler = new PacketHandler();
        getPacketHandler().registerPacket(new ConnectPacket());
        getPacketHandler().registerPacket(new DisconnectPacket());
        sendPacket(new ConnectPacket());
        new Thread(() -> {
            while (true) {
                try {
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    if(socket.getInputStream().available() > 0) {
                        new PacketDecoder(in, socket);
                        System.out.println("Got packet");
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String getConnectionName() {
        return connectionName;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public void stop() {
        try {
            sendPacket(new DisconnectPacket());
            if(!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet packet) {
        try {
            if(!socket.isClosed()) {
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.writeUTF(getSessionID());
                output.writeUTF(getConnectionName());
                output.writeInt(getPacketHandler().getPacketID(packet));
                packet.write(output);
                output.flush();
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSessionID() {
        return session;
    }

}
