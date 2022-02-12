package me.refluxo.networker.instance;

public class PacketInstance {

    private static PacketServiceType instance;

    public static void setInstance(PacketServiceType type) {
        instance = type;
    }

    public static PacketServiceType getInstance() {
        return instance;
    }

}
