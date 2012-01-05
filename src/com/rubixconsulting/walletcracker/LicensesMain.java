package com.rubixconsulting.walletcracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import com.google.android.apps.analytics.easytracking.TrackedActivity;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LicensesMain extends TrackedActivity {
  static protected final String TAG = LicensesMain.class.getSimpleName();
  static protected final Integer THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);
  static private Boolean lock = false;
  static private String versionName, licenseText;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.licenses);

    TextView licenseView = (TextView) findViewById(R.id.licenseCopy);
    synchronized(lock) {
      if (licenseText == null) {
        setLicenseText();
      }
      licenseView.setText(licenseText);
    }
  }

  private void setLicenseText() {
    BufferedReader r = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.licenses)));

    String line;
    StringBuilder sb = new StringBuilder();

    try {
      while ((line = r.readLine()) != null) {
        sb.append(line.trim());
        sb.append("\n");
      }

      r.close();
    } catch (IOException e) {
      // do nothing
      Log.e(TAG, "IOException: " + e.getMessage());
    }

    if (versionName == null) {
      try {
        versionName = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
      } catch (NameNotFoundException e) {
        versionName = getString(R.string.unknown_version);
      }
    }

    licenseText = sb.toString()
      .replace("@@app_name@@", getString(R.string.app_name))
      .replace("@@app_version@@", versionName)
      .replace("@@year@@", THIS_YEAR.toString());
  }
}