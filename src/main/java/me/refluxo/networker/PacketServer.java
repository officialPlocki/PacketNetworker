package me.refluxo.networker;

import me.refluxo.networker.instance.ConnectionManager;
import me.refluxo.networker.instance.PacketInstance;
import me.refluxo.networker.instance.PacketServiceType;
import me.refluxo.networker.instance.SocketThread;
import me.refluxo.networker.packet.Packet;
import me.refluxo.networker.packet.impl.ConnectPacket;
import me.refluxo.networker.packet.impl.DisconnectPacket;
import me.refluxo.networker.packethandler.PacketHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class PacketServer {

    private PacketHandler packetHandler;
    private ServerSocket socket;
    private String session;
    private String connectionName;
    public static PacketServer inst;
    private HashMap<Socket, SocketThread> socketThreads = null;

    public PacketServer() {
        inst = this;
        PacketInstance.setInstance(PacketServiceType.SERVER);
    }

    public void start(String sessionUUID, String connectionName, String host, int port) {
        socketThreads = new HashMap<>();
        ConnectionManager.init();
        this.connectionName = connectionName;
        this.session = sessionUUID;
        new Thread(() -> {
            try {
                socket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!socket.isClosed()) {
                try {
                    Socket s = socket.accept();
                    SocketThread socketThread = new SocketThread(s);
                    socketThread.start();
                    socketThreads.put(s, socketThread);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        packetHandler = new PacketHandler();
        getPacketHandler().registerPacket(new ConnectPacket());
        getPacketHandler().registerPacket(new DisconnectPacket());
    }

    public ServerSocket getSocket() {
        return socket;
    }

    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendPacket(Packet packet, Socket s) {
        if(!s.isClosed()) {
            socketThreads.get(s).sendPacket(packet);
        }
    }
    public void sendPacket(Packet packet, String connectionName) {
        Socket s = new ConnectionManager().getConnection(connectionName);
        if(!s.isClosed()) {
            socketThreads.get(s).sendPacket(packet);
        }
    }

    public String getConnectionName() {
        return connectionName;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public String getSessionID() {
        return session;
    }

}
