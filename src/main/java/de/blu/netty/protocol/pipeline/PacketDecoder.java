package de.blu.netty.protocol.pipeline;

import de.blu.netty.protocol.packet.Packet;
import de.blu.netty.protocol.packet.PacketRegistry;
import de.blu.netty.protocol.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public final class PacketDecoder extends ByteToMessageDecoder {
  @Override
  protected void decode(
      ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
    if (byteBuf instanceof EmptyByteBuf) {
      return;
    }

    // The packets size
    byteBuf.readInt();

    try {
      int packetId = byteBuf.readInt();
      Class<? extends Packet> packetClass = PacketRegistry.getPacketClasses().get(packetId);
      Packet packet = packetClass.newInstance();
      packet.setUniqueId(ByteBufUtils.readUUID(byteBuf));
      packet.read(byteBuf);
      list.add(packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
