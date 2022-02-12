package me.refluxo.networker.instance;

import java.net.Socket;
import java.util.HashMap;

public class ConnectionManager {

    private static HashMap<String, Socket> connections;

    public static void init() {
        connections = new HashMap<>();
    }

    public HashMap<String, Socket> getConnections() {
        return connections;
    }

    public void addConnection(String name, Socket socket) {
        connections.put(name, socket);
    }

    public Socket getConnection(String name) {
        return connections.get(name);
    }

    public void removeConnection(String name) {
        connections.remove(name);
    }

}
