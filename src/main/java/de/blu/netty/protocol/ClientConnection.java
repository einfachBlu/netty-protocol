package de.blu.netty.protocol;

import de.blu.netty.protocol.connection.ConnectionHandler;
import de.blu.netty.protocol.packet.Packet;
import de.blu.netty.protocol.response.ClientConnectResponse;
import de.blu.netty.protocol.util.Tuple;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public final class ClientConnection extends NetworkConnection {

  private ChannelFuture channelFuture;
  private ConnectionHandler connectionHandler;

  public void connect(
      String host, int port, Consumer<Tuple<ClientConnectResponse, Object>> clientConnectResponse) {
    try {
      NioEventLoopGroup workerGroup = new NioEventLoopGroup();

      Bootstrap bootstrap = new Bootstrap();
      bootstrap
          .group(workerGroup)
          .channel(NioSocketChannel.class)
          .handler(
              new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                  connectionHandler = preparePipeline(socketChannel);
                  connectionHandler.setName("Server");

                  new Timer()
                      .schedule(
                          new TimerTask() {
                            @Override
                            public void run() {
                              if (!socketChannel.isActive()) {
                                return;
                              }

                              clientConnectResponse.accept(
                                  new Tuple<>(
                                      ClientConnectResponse.CLIENT_CONNECTED, connectionHandler));
                            }
                          },
                          2000);
                }
              })
          .option(ChannelOption.SO_KEEPALIVE, true);

      ChannelFuture future = bootstrap.connect(host, port);
      this.getExecutorService()
          .execute(
              () -> {
                try {
                  this.channelFuture = future.sync().channel().closeFuture();

                  // Wait for shutdown
                  channelFuture.sync();
                  this.channelFuture = null;
                } catch (Exception e) {
                  if (e.getMessage().contains("Connection refused")) {
                    clientConnectResponse.accept(
                        new Tuple<>(ClientConnectResponse.CONNECTION_REFUSED, e));
                    return;
                  }
                  e.printStackTrace();
                }
              });
    } catch (Exception e) {
      clientConnectResponse.accept(new Tuple<>(ClientConnectResponse.ERROR, e));
    }
  }

  public void disconnect() {
    if (this.channelFuture == null) {
      return;
    }

    this.channelFuture.channel().close();
    this.channelFuture = null;
  }

  public void sendPacket(Packet packet) {
    this.connectionHandler.sendPacket(packet);
  }
}
