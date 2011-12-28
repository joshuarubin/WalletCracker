package com.zvelo.walletcracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

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
    final int messageId = getArguments().getInt("messageId");

    return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.error)
            .setMessage(messageId)
            .setCancelable(false)
            .setNeutralButton(R.string.error_dialog_ok, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
              }
            })
            .create();
  }
}
