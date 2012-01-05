package com.rubixconsulting.walletcracker;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {
  static private Boolean lock = false;
  static private String versionName;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View ret = inflater.inflate(R.layout.about, container, false);
    TextView versionView = (TextView) ret.findViewById(R.id.appVersion);
    synchronized(lock) {
      if (versionName == null) {
        setVersionName();
      }
      versionView.setText(versionName);
    }
    return ret;
  }

  private void setVersionName() {
    final Activity activity = getActivity();

    try {
      versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
    } catch (NameNotFoundException e) {
      versionName = activity.getString(R.string.unknown_version);
    }
  }
}
