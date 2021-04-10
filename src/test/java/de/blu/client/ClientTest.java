package de.blu.client;

import de.blu.common.packet.MessagePacket;
import de.blu.netty.protocol.ClientConnection;
import de.blu.netty.protocol.connection.ConnectionHandler;
import de.blu.netty.protocol.listener.ConnectionListener;
import de.blu.netty.protocol.packet.PacketRegistry;
import de.blu.netty.protocol.packet.SetNamePacket;
import de.blu.netty.protocol.response.ClientConnectResponse;

public final class ClientTest implements ConnectionListener {
  public static void main(String[] args) {
    new ClientTest();
  }

  private ClientConnection clientConnection;

  public ClientTest() {
    PacketRegistry.getPacketClasses().add(MessagePacket.class);

    // Start Server
    this.clientConnection = new ClientConnection();
    clientConnection.registerPacketListeners("de.blu.common.listener");
    clientConnection.registerPacketListeners("de.blu.client.listener");
    clientConnection.getConnectionListeners().add(this);
    this.connect();
  }

  private void connect() {
    clientConnection.connect(
        "localhost",
        8080,
        response -> {
          if (response.getFirst() == ClientConnectResponse.CLIENT_CONNECTED) {
            System.out.println("Connected!");
            return;
          }

          System.out.println("Could not connect to Server! Try again...");
          this.connect();
        });
  }

  @Override
  public void connected(ConnectionHandler connectionHandler) {
    this.clientConnection.sendPacket(new SetNamePacket("Player-1"));
  }

  @Override
  public void disconnected(ConnectionHandler connectionHandler) {
    System.out.println("Lost Connection to Server");
    this.connect();
  }
}
