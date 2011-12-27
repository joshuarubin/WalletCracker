package com.zvelo.walletcracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ListFragment;
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

public class DataFragment extends ListFragment implements WalletListener {
  protected final String TAG = this.getClass().getSimpleName();
  protected final String RESTORE = ", can restore state";

  protected TextView empty;
  protected SimpleAdapter listAdapter;
  protected List<Map<String, String>> listData;

  public void setWalletData(List<Map<String, String>> data) {
    listData.clear();
    listData.addAll(data);
    listAdapter.notifyDataSetChanged();
  }

  public void walletDataError(int error) {
    listData.clear();
    empty.setText(error);
    listAdapter.notifyDataSetChanged();
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // savedInstanceState could be null
    if (savedInstanceState != null) {
      // TODO
    }

    empty = (TextView) getActivity().findViewById(android.R.id.empty);
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

    load();

    Log.i(TAG, "onActivityCreated");
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.datalist, container, false);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    Log.i(TAG, "onCreateOptionsMenu");
    inflater.inflate(R.menu.datamenu, menu);
  }

  private void load() {
    BGLoader.addListener(this);
    reload();
  }

  public void reload() {
    empty.setText(R.string.loading);
    listData.clear();
    listAdapter.notifyDataSetChanged();
    new BGLoader().execute(getActivity());
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
    Log.i(TAG, "onOptionsItemSelected");
    switch (item.getItemId()) {
      case R.id.reload:
        reload();
        break;
    }
    return true;
  }
}
