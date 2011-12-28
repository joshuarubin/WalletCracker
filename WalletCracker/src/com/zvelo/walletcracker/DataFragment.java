package com.zvelo.walletcracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DataFragment extends ListFragment implements WalletListener {
  protected final String TAG = this.getClass().getSimpleName();
  protected final String RESTORE = ", can restore state";

  protected TextView statusView;
  protected SimpleAdapter listAdapter;
  protected List<Map<String, String>> listData;

  @Override public void walletProgress(BGLoader.Progress progress, Integer numSteps, DeviceInfoParser parser) {
    switch (progress) {
      case LOADED:
        showData(parser);
        break;
      default:
        clear();
    }
  }

  @Override public void walletLoaded(BGLoader.Status result, DeviceInfoParser parser) {
    switch (result) {
      case SUCCESS:
        showData(parser);
        break;
    }
  }

  private void clear() {
    listData.clear();
    listAdapter.notifyDataSetChanged();
  }

  private void showData(DeviceInfoParser parser) {
    listData.clear();
    listData.addAll(parser.getData());
    listAdapter.notifyDataSetChanged();
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // savedInstanceState could be null
    if (savedInstanceState != null) {
      // TODO
    }

    statusView = (TextView) getActivity().findViewById(android.R.id.empty);
    listData = new ArrayList<Map<String, String>>();
    listAdapter = new SimpleAdapter(
        getActivity(),
        listData,
        R.layout.datalistitem,
        new String[] {"title", "value"},
        new int[] {R.id.title,
                   R.id.value});
    setListAdapter(listAdapter);

    rebuild(false);

    Log.i(TAG, "onActivityCreated");
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.datalist, container, false);
  }

  public void rebuild(Boolean force) {
    clear();
    Log.i(TAG, "rebuild, force: "+force);
    new BGLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this, getActivity(), force);
  }
}