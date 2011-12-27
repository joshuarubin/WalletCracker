package com.zvelo.walletcracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

public final class BGLoader extends AsyncTask<Context, Void, TwoReturnValues<BGLoader.InitStatus, List<Map<String, String>>>> {
  protected final String TAG = this.getClass().getSimpleName();
  protected Context _context;
  static protected List<WalletListener> _listeners = new ArrayList<WalletListener>();
  static protected Boolean _loading = false;

  public enum InitStatus {
    SUCCESS,
    LOAD_ALREADY_IN_PROGRESS,
    NO_WALLET,
    NO_ROOT
  }

  static public void addListener(WalletListener listener) {
    _listeners.add(listener);
  }

  private void sendListenersData(List<Map<String, String>> data) {
    for (WalletListener listener : _listeners) {
      listener.setWalletData(data);
    }
  }

  private void sendListenersError(int error) {
    for (WalletListener listener : _listeners) {
      listener.walletDataError(error);
    }
  }

  // runs on the UI thread
  @Override protected void onPostExecute(TwoReturnValues<BGLoader.InitStatus, List<Map<String, String>>> result) {
    super.onPostExecute(result);

    switch (result.getFirst()) {
      case SUCCESS:
        sendListenersData(result.getSecond());
        break;
      case LOAD_ALREADY_IN_PROGRESS:
        Log.i(TAG, "Load already in progress");
        // NOTE: return to prevent _loading from being switched to false
        return;
      case NO_WALLET:
        Log.i(TAG, "Could not find wallet app installed");
        sendListenersError(R.string.wallet_not_found);
        break;
      case NO_ROOT:
        Log.i(TAG, "Could not gain root");
        sendListenersError(R.string.root_not_found);
        break;
      default:
        Log.i(TAG, "Unknown initialization error");
        sendListenersError(R.string.unknown_init_error);
    }

    synchronized(this) {
      _loading = false;
    }
  }

  // runs in its own thread
  @Override protected TwoReturnValues<InitStatus, List<Map<String, String>>> doInBackground(Context... params) {
    synchronized(this) {
      if (_loading) {
        return new TwoReturnValues<InitStatus, List<Map<String, String>>>(InitStatus.LOAD_ALREADY_IN_PROGRESS, null);
      }

      _loading = true;
    }

    _context = params[0];

    final PackageManager pm = _context.getPackageManager();
    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

    Boolean walletFound = false;
    for (ApplicationInfo packageInfo : packages) {
      if (packageInfo.packageName.equals(_context.getString(R.string.package_google_wallet))) {
        walletFound = true;
      }
    }

    if (!walletFound) {
      return new TwoReturnValues<InitStatus, List<Map<String, String>>>(InitStatus.NO_WALLET, null);
    } else if (!WalletCopier.canRunRootCommands()) {
      return new TwoReturnValues<InitStatus, List<Map<String, String>>>(InitStatus.NO_ROOT, null);
    }

    new WalletCopier(_context).execute();
    return new TwoReturnValues<InitStatus, List<Map<String, String>>>(InitStatus.SUCCESS, new DeviceInfoParser(_context).execute());
  }
}