package com.zvelo.walletcracker;

import java.util.List;
import java.util.Map;

public interface WalletListener {
  void setWalletData(List<Map<String, String>> data);
  void walletDataError(int error);
}
