package de.blu.netty.protocol;

import de.blu.netty.protocol.connection.ConnectionHandler;
import de.blu.netty.protocol.listener.ConnectionListener;
import de.blu.netty.protocol.listener.PacketListener;
import de.blu.netty.protocol.packet.Packet;
import de.blu.netty.protocol.packet.SetNamePacket;
import de.blu.netty.protocol.response.ServerStartResponse;
import de.blu.netty.protocol.util.Tuple;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ServerConnection extends NetworkConnection {

  private ChannelFuture channelFuture;
  @Getter protected List<ConnectionHandler> connections = new ArrayList<>();

  public ServerConnection() {
    this.getConnectionListeners()
        .add(
            new ConnectionListener() {
              @Override
              public void connected(ConnectionHandler connectionHandler) {
                connections.add(connectionHandler);
              }

              @Override
              public void disconnected(ConnectionHandler connectionHandler) {
                connections.remove(connectionHandler);
              }
            });

    this.registerPacketListener(
        SetNamePacket.class,
        new PacketListener<SetNamePacket>() {
          @Override
          public void handle(ConnectionHandler connectionHandler, SetNamePacket packet) {
            connectionHandler.setName(packet.getName());
          }

          @Override
          public Class<SetNamePacket> getPacketClass() {
            return SetNamePacket.class;
          }
        });
  }

  public void start(int port, Consumer<Tuple<ServerStartResponse, Throwable>> serverStartResponse) {
    try {
      NioEventLoopGroup bossGroup = new NioEventLoopGroup();
      NioEventLoopGroup workerGroup = new NioEventLoopGroup();

      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap
          .group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(
              new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                  preparePipeline(socketChannel);
                }
              })
          .childOption(ChannelOption.SO_KEEPALIVE, true);

      ChannelFuture future = serverBootstrap.bind(port);
      this.getExecutorService()
          .execute(
              () -> {
                try {
                  this.channelFuture = future.sync().channel().closeFuture();

                  this.getExecutorService()
                      .execute(
                          () -> {
                            serverStartResponse.accept(
                                new Tuple<>(ServerStartResponse.SUCCESSFULL, null));
                          });

                  // Waiting for Shutdown
                  this.channelFuture.sync();
                  System.out.println("Server stopped");
                  this.channelFuture = null;
                } catch (Exception e) {
                  if (e.getMessage().contains("Address already in use")) {
                    serverStartResponse.accept(
                        new Tuple<>(ServerStartResponse.PORT_ALREADY_IN_USE, e));
                    return;
                  }

                  e.printStackTrace();
                }
              });
    } catch (Exception e) {
      serverStartResponse.accept(new Tuple<>(ServerStartResponse.ERROR, e));
    }
  }

  public void stop() {
    if (this.channelFuture == null) {
      return;
    }

    this.channelFuture.channel().close();
    this.channelFuture = null;
  }

  public void broadcastPacket(Packet packet) {
    for (ConnectionHandler connection : this.getConnections()) {
      connection.sendPacket(packet);
    }
  }

  public void sendPacket(ConnectionHandler connectionHandler, Packet packet) {
    connectionHandler.sendPacket(packet);
  }

  public ConnectionHandler getConnectionByName(String name) {
    return this.getConnections().stream()
        .filter(connectionHandler -> connectionHandler.getName().equals(name))
        .findFirst()
        .orElse(null);
  }
}
