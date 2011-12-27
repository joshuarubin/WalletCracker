package com.zvelo.walletcracker;

public class TwoReturnValues<K, V> {
  private final K _first;
  private final V _second;

  public TwoReturnValues(K first, V second) {
    _first = first;
    _second = second;
  }

  public K getFirst() {
    return _first;
  }

  public V getSecond() {
    return _second;
  }
}