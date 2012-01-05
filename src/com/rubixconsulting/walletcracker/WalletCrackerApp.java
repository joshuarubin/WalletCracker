package com.rubixconsulting.walletcracker;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dGFJR0hPMFBnVmhpTHpYbVEzVW9vT0E6MQ")

public class WalletCrackerApp extends Application {
  @Override public void onCreate() {
    ACRA.init(this);
    super.onCreate();
  }
}