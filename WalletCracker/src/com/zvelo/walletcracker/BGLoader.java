package com.zvelo.walletcracker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

public final class BGLoader extends AsyncTask<Object, BGLoader.Progress, BGLoader.Status> {
  protected final String TAG = this.getClass().getSimpleName();
  static private Set<WalletListener> _listeners = new HashSet<WalletListener>();
  static private Boolean _loading = false;
  static private Boolean _loaded = false;

  public enum Progress {
    LOADING,
    LOADED,
  }

  public enum Status {
    SUCCESS,
    NO_WALLET,
    NO_ROOT,
    PROGRESS_UPDATE_ONLY,
  }

  static public Boolean loaded() {
    synchronized(_loaded) {
      return _loaded;
    }
  }

  static public void addListener(WalletListener listener) {
    synchronized(_listeners) {
      if (listener != null) {
        _listeners.add(listener);
      }
    }
  }

  // runs on the UI thread
  @Override protected void onPostExecute(Status result) {
    super.onPostExecute(result);

    synchronized(_listeners) {
      for (WalletListener listener : _listeners) {
        listener.walletLoaded(result);
      }
    }

    if (result == Status.PROGRESS_UPDATE_ONLY) {
      return;
    }

    synchronized(_loaded) {
      _loaded = true;
    }

    synchronized(_loading) {
      _loading = false;
    }
  }

  // runs on the UI thread
  @Override protected void onProgressUpdate(Progress... params) {
    final Progress progress = params[0];

    synchronized(_listeners) {
      for (WalletListener listener : _listeners) {
        listener.walletProgress(progress);
      }
    }
  }

  // runs in its own thread
  @Override protected Status doInBackground(Object... params) {
    WalletListener listener = null;
    Context context = null;
    Boolean force = null;

    try {
      listener = (WalletListener) params[0];
      context = (Context) params[1];
      force = (Boolean) params[2];
      addListener(listener);
    } catch (ClassCastException e) {
      context = (Context) params[0];
      force = (Boolean) params[1];
    }

    synchronized(_loading) {
      if (_loading) {
        Log.i(TAG, "Load already in progress");
        publishProgress(Progress.LOADING);
        return Status.PROGRESS_UPDATE_ONLY;
      }
    }

    synchronized(_loaded) {
      if (!force && _loaded) {
        Log.i(TAG, "Already loaded, force not requested");
        publishProgress(Progress.LOADED);
        return Status.PROGRESS_UPDATE_ONLY;
      }
    }

    synchronized(_loading) {
      _loading = true;
    }

    publishProgress(Progress.LOADING);

    final PackageManager pm = context.getPackageManager();
    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

    Boolean walletFound = false;
    for (ApplicationInfo packageInfo : packages) {
      if (packageInfo.packageName.equals(context.getString(R.string.package_google_wallet))) {
        walletFound = true;
      }
    }

    if (!walletFound) {
      Log.i(TAG, "Could not find wallet app installed");
      return Status.NO_WALLET;
    } else if (!WalletCopier.canRunRootCommands()) {
      Log.i(TAG, "Could not gain root");
      return Status.NO_ROOT;
    }

    new WalletCopier(context).execute();

    return Status.SUCCESS;
  }
}