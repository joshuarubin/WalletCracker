package com.rubixconsulting.walletcracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;

public class ErrorDialogFragment extends DialogFragment {
  static public final String TAG = "ErrorDialogFragment";

  public static ErrorDialogFragment newInstance(int messageId) {
    ErrorDialogFragment fragment = new ErrorDialogFragment();

    Bundle args = new Bundle();
    args.putInt("messageId", messageId);
    fragment.setArguments(args);

    return fragment;
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    super.onCreateDialog(savedInstanceState);
    final int messageId = getArguments().getInt("messageId");
    Log.i(TAG, "onCreateDialog");

    if (messageId == 0) {
      Log.d(TAG, "Not creating invalid error");
      return null;
    }

    setCancelable(false);

    return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.error)
            .setMessage(messageId)
            .setCancelable(false)
            .setIcon(R.drawable.ic_dialog_alert_holo_light)
            .setNeutralButton(R.string.error_dialog_ok, null)
            .create();
  }
}
