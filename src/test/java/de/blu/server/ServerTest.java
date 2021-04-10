package de.blu.server;

import de.blu.common.packet.MessagePacket;
import de.blu.netty.protocol.ServerConnection;
import de.blu.netty.protocol.connection.ConnectionHandler;
import de.blu.netty.protocol.listener.ConnectionListener;
import de.blu.netty.protocol.packet.PacketRegistry;

public final class ServerTest implements ConnectionListener {
  public static void main(String[] args) {
    new ServerTest();
  }

  private ServerConnection serverConnection;

  public ServerTest() {
    PacketRegistry.getPacketClasses().add(MessagePacket.class);

    // Start Server
    this.serverConnection = new ServerConnection();
    serverConnection.registerPacketListeners("de.blu.common.listener");
    serverConnection.registerPacketListeners("de.blu.server.listener");
    serverConnection.getConnectionListeners().add(this);
    serverConnection.start(
        8080,
        response -> {
          switch (response.getFirst()) {
            case SUCCESSFULL:
              System.out.println("Server started.");
              return;
          }

          System.out.println("Server could not start! " + response.getSecond().getMessage());
        });
  }

  @Override
  public void connected(ConnectionHandler connectionHandler) {
    System.out.println("Client connected with name " + connectionHandler.getName());
    this.serverConnection.broadcastPacket(
        new MessagePacket(connectionHandler.getName() + " connected to the cluster!"));
  }

  @Override
  public void disconnected(ConnectionHandler connectionHandler) {
    System.out.println("Client disconnected with name " + connectionHandler.getName());
    this.serverConnection.broadcastPacket(
        new MessagePacket(connectionHandler.getName() + " disconnected from the cluster!"));
  }
}
