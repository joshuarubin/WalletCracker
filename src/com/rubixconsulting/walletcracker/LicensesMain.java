package com.rubixconsulting.walletcracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.android.apps.analytics.easytracking.TrackedActivity;
import com.zvelo.walletcracker.R;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LicensesMain extends TrackedActivity {
  protected final String TAG = this.getClass().getSimpleName();

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.licenses);

    TextView licenseView = (TextView) findViewById(R.id.licenseCopy);
    licenseView.setText(getTextResource(R.raw.licenses));
  }

  private String getTextResource(int resourceId) {
    BufferedReader r = new BufferedReader(new InputStreamReader(getResources().openRawResource(resourceId)));

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

    Log.d(TAG, sb.toString());
    return sb.toString().replace("@@app_name@@", getString(R.string.app_name));
  }
}