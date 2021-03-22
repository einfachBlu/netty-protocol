package de.blu.netty.protocol.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class ByteBufUtils {
  public static String readString(ByteBuf byteBuf) {
    int length = byteBuf.readInt();
    byte[] bytes = new byte[length];

    if (length > byteBuf.readableBytes()) {
      return null;
    }

    byteBuf.readBytes(bytes);
    return new String(bytes);
  }

  public static void writeString(ByteBuf byteBuf, String string) {
    byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
    byteBuf.writeInt(bytes.length);
    byteBuf.writeBytes(bytes);
  }

  public static UUID readUUID(ByteBuf byteBuf) {
    String uuidString = ByteBufUtils.readString(byteBuf);

    if (uuidString == null) {
      return null;
    }

    return UUID.fromString(uuidString);
  }

  public static void writeUUID(ByteBuf byteBuf, UUID uuid) {
    ByteBufUtils.writeString(byteBuf, uuid.toString());
  }
}
