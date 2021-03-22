package de.blu.netty.protocol.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class Tuple<T1, T2> {
  @Getter private T1 first;
  @Getter private T2 second;
}
