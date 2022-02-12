package me.refluxo.networker.instance;

import me.refluxo.networker.PacketServer;
import me.refluxo.networker.packet.Packet;
import me.refluxo.networker.packethandler.PacketDecoder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketThread extends Thread {

    private final Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public SocketThread(Socket socket) {
        this.socket = socket;
        try {
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                assert input != null;
                if(input.available() > 0) {
                    new PacketDecoder(input, socket);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            assert input != null;
            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet packet) {
        try {
            if(!PacketServer.inst.getSocket().isClosed()) {
                if(!socket.isClosed()) {
                    output.writeUTF(PacketServer.inst.getSessionID());
                    output.writeUTF(PacketServer.inst.getConnectionName());
                    output.writeInt(PacketServer.inst.getPacketHandler().getPacketID(packet));
                    packet.write(output);
                    output.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
