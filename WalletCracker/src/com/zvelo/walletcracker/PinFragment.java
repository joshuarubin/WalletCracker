package com.zvelo.walletcracker;

import com.zvelo.walletcracker.BGLoader.Progress;
import com.zvelo.walletcracker.BGLoader.Status;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PinFragment extends Fragment implements WalletListener {
  protected final String TAG = this.getClass().getSimpleName();
  protected final String RESTORE = ", can restore state";

  protected TextView pinValue;
  protected View dataView;

  @Override public void walletProgress(Progress progress, Integer numSteps, DeviceInfoParser parser) {
    switch (progress) {
      case LOADED:
        showData(parser);
        break;
      default:
        clear();
    }
  }

  @Override public void walletLoaded(Status result, DeviceInfoParser parser) {
    switch (result) {
      case SUCCESS:
        showData(parser);
        break;
    }
  }

  private void clear() {
    dataView.setVisibility(View.GONE);
  }

  private void showData(DeviceInfoParser parser) {
    pinValue.setText(DeviceInfoParser.formatPin(parser.crackPin()));
    dataView.setVisibility(View.VISIBLE);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // savedInstanceState could be null
    if (savedInstanceState != null) {
      // TODO
    }

    pinValue = (TextView) getActivity().findViewById(R.id.pinValue);
    dataView = getActivity().findViewById(R.id.pinData);

    rebuild(false);

    Log.i(TAG, "onActivityCreated");
  }

  public void rebuild(Boolean force) {
    clear();
    Log.i(TAG, "rebuild, force: "+force);
    new BGLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this, getActivity(), force);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.pin, container, false);
  }
}
