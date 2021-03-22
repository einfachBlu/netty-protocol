package de.blu.netty.protocol.listener;

import de.blu.netty.protocol.connection.ConnectionHandler;

public interface ConnectionListener {
  void connected(ConnectionHandler connectionHandler);

  void disconnected(ConnectionHandler connectionHandler);
}
