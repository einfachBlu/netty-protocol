package de.blu.common.packet;

import de.blu.netty.protocol.packet.Packet;
import de.blu.netty.protocol.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public final class MessagePacket extends Packet {

  private String message;

  public MessagePacket() {}

  public MessagePacket(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public void write(ByteBuf byteBuf) {
    ByteBufUtils.writeString(byteBuf, this.message);
  }

  @Override
  public void read(ByteBuf byteBuf) {
    this.message = ByteBufUtils.readString(byteBuf);
  }
}
