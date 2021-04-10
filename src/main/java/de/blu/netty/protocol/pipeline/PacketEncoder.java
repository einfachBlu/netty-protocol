package de.blu.netty.protocol.pipeline;

import de.blu.netty.protocol.packet.Packet;
import de.blu.netty.protocol.packet.PacketRegistry;
import de.blu.netty.protocol.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public final class PacketEncoder extends MessageToByteEncoder<Packet> {

  @Override
  protected void encode(
      ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
    Class<? extends Packet> packetClass = PacketRegistry.getByName(packet.getClass().getSimpleName());
    int packetId = PacketRegistry.getPacketClasses().indexOf(packetClass);

    try {
      byteBuf.writeInt(packetId);
      ByteBufUtils.writeUUID(byteBuf, packet.getUniqueId());
      packet.write(byteBuf);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
