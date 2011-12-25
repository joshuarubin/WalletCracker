package com.zvelo.walletcracker;

import java.util.ArrayList;
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
  protected final Context context = this;

  protected List<Map<String, String>> listData;
  protected BGLoader bGLoader;
  protected TextView empty;
  protected SimpleAdapter listAdapter;

  protected enum InitStatus {
   SUCCESS,
   NO_WALLET,
   NO_ROOT
  }

  protected final class BGLoader extends AsyncTask<Void, Void, InitStatus> {
    protected final String TAG = this.getClass().getSimpleName();

    // runs on the UI thread
    @Override protected void onPostExecute(InitStatus result) {
      super.onPostExecute(result);

      if (result == InitStatus.SUCCESS) {
        DeviceInfoParser parser = new DeviceInfoParser(context);
        listData.clear();
        listData.addAll(parser.execute());
        listAdapter.notifyDataSetChanged();
      } else {
        listData.clear();
        if (result == InitStatus.NO_WALLET) {
          empty.setText(getString(R.string.wallet_not_found));
          Log.d(TAG, "Could not find wallet app installed");
        } else if (result == InitStatus.NO_ROOT) {
          empty.setText(getString(R.string.root_not_found));
          Log.d(TAG, "Could not gain root");
        } else {
          empty.setText(getString(R.string.unknown_init_error));
          Log.d(TAG, "Unknown initialization error");
        }
        listAdapter.notifyDataSetChanged();
      }
    }

    // runs in its own thread
    @Override protected InitStatus doInBackground(Void... params) {
      final PackageManager pm = context.getPackageManager();
      List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

      Boolean walletFound = false;
      for (ApplicationInfo packageInfo : packages) {
        if (packageInfo.packageName.equals(getString(R.string.package_google_wallet))) {
          walletFound = true;
        }
      }

      if (!walletFound) {
        return InitStatus.NO_WALLET;
      } else if (!WalletCopier.canRunRootCommands()) {
        return InitStatus.NO_ROOT;
      }

      WalletCopier walletCopier = new WalletCopier(context);
      walletCopier.execute();

      return InitStatus.SUCCESS;
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

    bGLoader = new BGLoader();
    bGLoader.execute();

    Log.i(TAG, "onCreate");
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