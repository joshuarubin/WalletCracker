package com.zvelo.walletcracker;

import com.zvelo.walletcracker.BGLoader.Progress;
import com.zvelo.walletcracker.BGLoader.Status;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PinFragment extends Fragment implements WalletListener {
  protected final String TAG = this.getClass().getSimpleName();
  protected final String RESTORE = ", can restore state";

  protected TextView statusView;
  protected TextView pinValue;
  protected View dataView;

  @Override public void walletLoaded(Status result, DeviceInfoParser parser) {
    switch (result) {
      case SUCCESS:
        showData(parser);
        break;
      case NO_WALLET:
        showError(R.string.wallet_not_found);
        break;
      case NO_ROOT:
        showError(R.string.root_not_found);
        break;
    }
  }

  @Override public void walletProgress(Progress progress, DeviceInfoParser parser) {
    switch (progress) {
      case LOADING:
        showLoading();
        break;
      case LOADED:
        showData(parser);
        break;
    }
  }

  private void showData(DeviceInfoParser parser) {
    pinValue.setText(DeviceInfoParser.formatPin(parser.crackPin()));
    statusView.setVisibility(View.GONE);
    dataView.setVisibility(View.VISIBLE);
  }

  private void showLoading() {
    showError(R.string.loading);
  }
  
  private void showError(int stringId) {
    statusView.setText(stringId);
    dataView.setVisibility(View.GONE);
    statusView.setVisibility(View.VISIBLE);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // savedInstanceState could be null
    if (savedInstanceState != null) {
      // TODO
    }

    pinValue = (TextView) getActivity().findViewById(R.id.pinValue);
    statusView = (TextView) getActivity().findViewById(R.id.pinStatus);
    dataView = getActivity().findViewById(R.id.pinData);

    rebuild(false);

    Log.i(TAG, "onActivityCreated");
  }

  public void rebuild(Boolean force) {
    showLoading();
    Log.i(TAG, "PinFragment rebuild");
    new BGLoader().execute(this, getActivity(), force);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.pin, container, false);
  }
}
