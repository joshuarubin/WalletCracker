package com.zvelo.walletcracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ListFragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DataFragment extends ListFragment {
  protected final String TAG = this.getClass().getSimpleName();
  protected final String RESTORE = ", can restore state";

  protected enum InitStatus {
    SUCCESS,
    NO_WALLET,
    NO_ROOT
   }

  protected TextView empty;
  protected SimpleAdapter listAdapter;
  protected List<Map<String, String>> listData;
  protected BGLoader bGLoader;

  protected final class BGLoader extends AsyncTask<Void, Void, InitStatus> {
    protected final String TAG = this.getClass().getSimpleName();

    // runs on the UI thread
    @Override protected void onPostExecute(InitStatus result) {
      super.onPostExecute(result);

      if (result == InitStatus.SUCCESS) {
        DeviceInfoParser parser = new DeviceInfoParser(getActivity());
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
      final PackageManager pm = getActivity().getPackageManager();
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

      WalletCopier walletCopier = new WalletCopier(getActivity());
      walletCopier.execute();

      return InitStatus.SUCCESS;
    }
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // savedInstanceState could be null
    if (savedInstanceState != null) {
      // TODO
    }

    empty = (TextView) getActivity().findViewById(android.R.id.empty);
    empty.setText(getString(R.string.loading));

    listData = new ArrayList<Map<String, String>>();

    listAdapter = new SimpleAdapter(
        getActivity(),
        listData,
        R.layout.datalistitem,
        new String[] {"title", "value"},
        new int[] {R.id.title,
                   R.id.value});

    setListAdapter(listAdapter);
    setHasOptionsMenu(true);

    bGLoader = new BGLoader();
    bGLoader.execute();

    Log.i(TAG, "onActivityCreated");
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.datalist, container, false);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    Log.i(TAG, "onCreateOptionsMenu");
    inflater.inflate(R.menu.datamenu, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
    Log.i(TAG, "onOptionsItemSelected");
    switch (item.getItemId()) {
      case R.id.reload:
        Log.d(TAG, "TODO reload");
        // TODO
    }
    return true;
  }
}
