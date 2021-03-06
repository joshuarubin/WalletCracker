package com.rubixconsulting.walletcracker;

import com.rubixconsulting.walletcracker.BGLoader.Progress;
import com.rubixconsulting.walletcracker.BGLoader.Status;
import com.rubixconsulting.walletcracker.DeviceInfoParser.Exception;

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

  @Override public void walletProgress(Progress progress, Integer numSteps) {
    Log.i(TAG, "got progress: "+progress.toString());
    clear();
  }

  @Override public void walletLoaded(Status result, DeviceInfoParser parser) {
    Log.i(TAG, "got loaded: "+result.toString());
    if (result == BGLoader.Status.SUCCESS) {
      showData(parser);
    }
  }

  private void clear() {
    dataView.setVisibility(View.GONE);
  }

  private void showData(DeviceInfoParser parser) {
    String value;
    try {
      final Integer pin = parser.crackPin();
      value = DeviceInfoParser.formatPin(pin);
    } catch (Exception e) {
      // error cracking pin, ignore
      Log.e(TAG, e.getMessage());
      value = e.getMessage();
    }
    pinValue.setText(value);
    dataView.setVisibility(View.VISIBLE);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    pinValue = (TextView) getActivity().findViewById(R.id.pinValue);
    dataView = getActivity().findViewById(R.id.pinData);

    Log.i(TAG, "onActivityCreated");
  }

  public void rebuild(Boolean force) {
    clear();
    Log.i(TAG, "rebuild, force: "+force);
    new BGLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity(), force);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.i(TAG, "onCreateView");
    return inflater.inflate(R.layout.pin, container, false);
  }

  @Override public void onPause() {
    super.onPause();
    Log.i(TAG, "onPause" + (getActivity().isFinishing() ? "Finishing" : ""));
    BGLoader.removeListener(this);
  }

  @Override public void onResume() {
    super.onResume();
    Log.i(TAG, "onResume");
    BGLoader.addListener(this);
    if (dataView.getVisibility() == View.GONE) {
      rebuild(false);
    }
  }
}
