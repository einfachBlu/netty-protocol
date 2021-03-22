package de.blu.netty.protocol.packet;

import de.blu.netty.protocol.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
/**
 * This Packet can be used to give the Client Connection a custom Name to
 * indentify that easier later
 */
public final class SetNamePacket extends Packet {

  private String name;

  @Override
  public void write(ByteBuf byteBuf) {
    ByteBufUtils.writeString(byteBuf, this.name);
  }

  @Override
  public void read(ByteBuf byteBuf) {
    this.name = ByteBufUtils.readString(byteBuf);
  }
}
