package com.zvelo.walletcracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class WalletCrackerMain extends ListActivity {
  // Make strings for logging
  protected final String TAG = this.getClass().getSimpleName();
  protected final String RESTORE = ", can restore state";
  protected final String PACKAGE_GOOGLE_WALLET = "com.google.android.apps.walletnfcrel";

  protected List<Map<String, String>> listData;
  protected WalletFinder walletFinder;
  protected TextView empty;
  protected SimpleAdapter listAdapter;

  protected final class WalletFinder extends AsyncTask<Context, Void, Boolean> {
    protected final String TAG = this.getClass().getSimpleName();

    // runs on the UI thread
    @Override protected void onPostExecute(Boolean result) {
      super.onPostExecute(result);

      if (result) {
        // TODO
        listData.clear();
        addToList("Found Google Wallet", PACKAGE_GOOGLE_WALLET);
        listAdapter.notifyDataSetChanged();
        Log.d(TAG, "Found wallet app");
      } else {
        listData.clear();
        empty.setText(getString(R.string.wallet_not_found));
        listAdapter.notifyDataSetChanged();
        Log.d(TAG, "Could not find wallet app installed");
      }
    }

    // runs in its own thread
    @Override protected Boolean doInBackground(Context... params) {
      final Context context = params[0];
      final PackageManager pm = context.getPackageManager();
      List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

      for (ApplicationInfo packageInfo : packages) {
        if (packageInfo.packageName.equals(PACKAGE_GOOGLE_WALLET)) {
          return true;
        }
      }

      return false;
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // savedInstanceState could be null
    if (savedInstanceState != null) {
      // TODO
    }

    setContentView(R.layout.main);
    empty = (TextView) findViewById(android.R.id.empty);
    empty.setText(getString(R.string.loading));

    listData = new ArrayList<Map<String, String>>();

    listAdapter = new SimpleAdapter(this, listData,
        R.layout.listitem,
        new String[] {"title", "value"},
        new int[] {R.id.title,
                   R.id.value});
    setListAdapter(listAdapter);

    walletFinder = new WalletFinder();
    walletFinder.execute(this);

    Log.i(TAG, "onCreate");
  }

  private void addToList(String title, String value) {
    Map<String, String> map = new HashMap<String, String>(2);
    map.put("title", title);
    map.put("value", value);
    listData.add(map);
  }

  @Override protected void onRestart() {
    super.onRestart();
    Log.i(TAG, "onRestart");
  }

  @Override protected void onStart() {
    super.onStart();
    Log.i(TAG, "onStart");
  }

  @Override protected void onResume() {
    super.onResume();
    Log.i(TAG, "onResume");
  }

  @Override protected void onPause() {
    super.onPause();
    Log.i(TAG, "onPause" + (isFinishing() ? "Finishing" : ""));
  }

  @Override protected void onStop() {
    super.onStop();
    Log.i(TAG, "onStop");
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "onDestroy" + Integer.toString(getChangingConfigurations(), 16));
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Log.i(TAG, "onSaveInstanceState");
  }

  @Override public Object onRetainNonConfigurationInstance() {
    Log.i(TAG, "onRetainNonConfigurationInstance");
    return new Integer(getTaskId());
  }

  @Override protected void onRestoreInstanceState(Bundle savedState) {
    super.onRestoreInstanceState(savedState);
    Object oldTaskObject = getLastNonConfigurationInstance();
    if (oldTaskObject != null) {
      int oldTask = ((Integer) oldTaskObject).intValue();
      int currentTask = getTaskId();
      assert oldTask == currentTask;
    }
    Log.i(TAG, "onRestoreInstanceState" + (null == savedState ? "" : RESTORE));
  }

  @Override protected void onPostCreate(Bundle savedState) {
    super.onPostCreate(savedState);
    if (savedState != null ) {
      // TODO
    }
    Log.i(TAG, "onPostCreate" + (savedState == null ? "" : RESTORE));
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    Log.i(TAG, "onPostResume");
  }

  @Override protected void onUserLeaveHint() {
    super.onUserLeaveHint();
    Log.i(TAG, "onUserLeaveHint");
  }
}