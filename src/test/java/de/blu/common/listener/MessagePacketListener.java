package de.blu.common.listener;

import de.blu.common.packet.MessagePacket;
import de.blu.netty.protocol.connection.ConnectionHandler;
import de.blu.netty.protocol.listener.PacketListener;

public final class MessagePacketListener implements PacketListener<MessagePacket> {

  @Override
  public void handle(ConnectionHandler connectionHandler, MessagePacket packet) {
    System.out.println(packet.getMessage());
  }

  @Override
  public Class<MessagePacket> getPacketClass() {
    return MessagePacket.class;
  }
}
