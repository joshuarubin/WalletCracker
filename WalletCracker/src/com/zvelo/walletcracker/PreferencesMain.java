package com.zvelo.walletcracker;

import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class PreferencesMain extends PreferenceActivity implements OnSharedPreferenceChangeListener {
  protected final String TAG = this.getClass().getSimpleName();
  private static Boolean registered = false;

  public static class DataPreferences extends PreferenceFragment {
    @Override public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.data_preferences);
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // TODO: implement event based listener removal and remove the registered flag
    synchronized(registered) {
      if (!registered) {
        registered = true;
        Log.i(TAG, "registering pref listener");
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
      }
    }
  }

  @Override public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.preference_headers, target);
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals("demo_mode")) {
      rebuild(true);
    }
  }

  private void rebuild(Boolean force) {
    Log.i(TAG, "rebuild, force: "+force);
    new BGLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this, force);
  }
}