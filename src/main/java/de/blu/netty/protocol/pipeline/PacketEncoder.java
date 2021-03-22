package de.blu.netty.protocol.pipeline;

import de.blu.netty.protocol.packet.Packet;
import de.blu.netty.protocol.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public final class PacketEncoder extends MessageToByteEncoder<Packet> {

  @Override
  protected void encode(
      ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
    ByteBufUtils.writeString(byteBuf, packet.getClass().getName());
    ByteBufUtils.writeUUID(byteBuf, packet.getUniqueId());
    packet.write(byteBuf);
  }
}
