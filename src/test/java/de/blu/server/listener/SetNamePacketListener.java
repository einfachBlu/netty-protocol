package de.blu.server.listener;

import de.blu.netty.protocol.connection.ConnectionHandler;
import de.blu.netty.protocol.listener.PacketListener;
import de.blu.netty.protocol.packet.SetNamePacket;

public final class SetNamePacketListener implements PacketListener<SetNamePacket> {

  @Override
  public void handle(ConnectionHandler connectionHandler, SetNamePacket packet) {
    System.out.println("Client changed the name to " + packet.getName());
  }

  @Override
  public Class<SetNamePacket> getPacketClass() {
    return SetNamePacket.class;
  }
}
