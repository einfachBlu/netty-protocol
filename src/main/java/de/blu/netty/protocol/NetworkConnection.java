package de.blu.netty.protocol;

import de.blu.netty.protocol.connection.ConnectionHandler;
import de.blu.netty.protocol.listener.ConnectionListener;
import de.blu.netty.protocol.listener.PacketListener;
import de.blu.netty.protocol.packet.Packet;
import de.blu.netty.protocol.pipeline.PacketDecoder;
import de.blu.netty.protocol.pipeline.PacketEncoder;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class NetworkConnection {

  @Getter private ExecutorService executorService = Executors.newCachedThreadPool();
  @Getter private List<ConnectionListener> connectionListeners = new ArrayList<>();

  @Getter
  private Map<Class<? extends Packet>, Collection<PacketListener<? extends Packet>>>
      packetListeners = new HashMap<>();

  protected ConnectionHandler preparePipeline(SocketChannel socketChannel) {
    ConnectionHandler connectionHandler = new ConnectionHandler(this);

    socketChannel.pipeline().addLast(new LengthFieldPrepender(8, true));
    socketChannel.pipeline().addLast(new PacketDecoder());
    socketChannel.pipeline().addLast(new PacketEncoder());
    socketChannel.pipeline().addLast(connectionHandler);

    return connectionHandler;
  }

  public void registerPacketListener(
      Class<? extends Packet> packetClass, PacketListener<? extends Packet> packetListener) {
    if (!this.getPacketListeners().containsKey(packetClass)) {
      this.getPacketListeners().put(packetClass, new ArrayList<>());
    }

    this.getPacketListeners().get(packetClass).add(packetListener);
  }

  public void registerPacketListeners(String packageName) {
    try {
      Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
      for (Class<? extends PacketListener> packetListenerClass :
              reflections.getSubTypesOf(PacketListener.class)) {
        PacketListener packetListener = packetListenerClass.newInstance();
        this.registerPacketListener(packetListener.getPacketClass(), packetListener);
      }
    } catch (Exception e) {
      return;
    }
  }
}
