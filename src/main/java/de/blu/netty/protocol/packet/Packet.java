package de.blu.netty.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
public abstract class Packet {

  /** Unique identifier for this Packet */
  @Getter @Setter private UUID uniqueId = UUID.randomUUID();

  /**
   * Writes the Packet into a ByteBuf
   *
   * @param byteBuf the ByteBuf
   */
  public abstract void write(ByteBuf byteBuf);

  /**
   * Reads Packet data from a ByteBuf
   *
   * @param byteBuf the ByteBuf
   */
  public abstract void read(ByteBuf byteBuf);
}
