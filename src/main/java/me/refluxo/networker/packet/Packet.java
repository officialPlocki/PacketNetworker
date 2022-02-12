package me.refluxo.networker.packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface Packet {

    void read(ObjectInputStream in) throws IOException;
    void write(ObjectOutputStream out) throws IOException;

}
