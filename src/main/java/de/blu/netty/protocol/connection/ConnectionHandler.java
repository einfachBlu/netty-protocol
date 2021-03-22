package de.blu.netty.protocol.connection;

import de.blu.netty.protocol.NetworkConnection;
import de.blu.netty.protocol.listener.ConnectionListener;
import de.blu.netty.protocol.listener.PacketListener;
import de.blu.netty.protocol.packet.Packet;
import de.blu.netty.protocol.response.ClientConnectResponse;
import de.blu.netty.protocol.util.Tuple;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ConnectionHandler extends SimpleChannelInboundHandler<Packet> {

  @Getter @Setter private String name = "Unnamed-" + this.hashCode();
  @Getter private Channel channel;
  private final List<Packet> sendPacketsBeforeConnected = new ArrayList<>();
  private final NetworkConnection networkConnection;

  public ConnectionHandler(NetworkConnection networkConnection) {
    this.networkConnection = networkConnection;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    this.channel = ctx.channel();

    for (Packet packet : this.sendPacketsBeforeConnected) {
      this.sendPacket(packet);
    }

    for (ConnectionListener listener : this.networkConnection.getConnectionListeners()) {
      listener.connected(this);
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    this.channel = null;

    for (ConnectionListener listener : this.networkConnection.getConnectionListeners()) {
      listener.disconnected(this);
    }
  }

  public void sendPacket(Packet packet) {
    if (this.channel == null) {
      this.sendPacketsBeforeConnected.add(packet);
    } else {
      this.channel.writeAndFlush(packet);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    // super.exceptionCaught(ctx, cause);
    ctx.close();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
    Collection<PacketListener<? extends Packet>> listeners =
        this.networkConnection
            .getPacketListeners()
            .getOrDefault(packet.getClass(), new ArrayList<>());

    for (PacketListener listener : listeners) {
      listener.handle(this, packet);
    }
  }
}
