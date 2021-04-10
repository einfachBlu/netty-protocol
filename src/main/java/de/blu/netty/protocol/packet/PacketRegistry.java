package de.blu.netty.protocol.packet;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public final class PacketRegistry {

  @Getter
  private static List<Class<? extends Packet>> packetClasses =
      new ArrayList<Class<? extends Packet>>() {
        {
          this.add(SetNamePacket.class);
        }
      };

  public static Class<? extends Packet> getByName(String packetClassName) {
    return packetClasses.stream()
        .filter(aClass -> aClass.getName().equals(packetClassName) || aClass.getSimpleName().equals(packetClassName))
        .findFirst()
        .orElse(null);
  }
}
