package com.zvelo.walletcracker;

public interface WalletListener {
  void walletLoaded(BGLoader.Status result);
  void walletProgress(BGLoader.Progress progress);
}
