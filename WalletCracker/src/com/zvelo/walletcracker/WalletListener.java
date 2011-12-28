package com.zvelo.walletcracker;

public interface WalletListener {
  void walletLoaded(BGLoader.Status result, DeviceInfoParser parser);
  void walletProgress(BGLoader.Progress progress, Integer numSteps);
}
