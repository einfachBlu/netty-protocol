package de.blu.netty.protocol.listener;

import de.blu.netty.protocol.connection.ConnectionHandler;
import de.blu.netty.protocol.packet.Packet;

public interface PacketListener<T extends Packet> {
  void handle(ConnectionHandler connectionHandler, T packet);

  Class<T> getPacketClass();
}
