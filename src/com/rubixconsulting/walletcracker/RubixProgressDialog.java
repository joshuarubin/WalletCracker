package com.rubixconsulting.walletcracker;

import android.app.ProgressDialog;
import android.content.Context;

public class RubixProgressDialog extends ProgressDialog {
  public RubixProgressDialog(Context context) {
    super(context);
  }

  public RubixProgressDialog(Context context, int theme) {
    super(context, theme);
  }

  public void update(Integer messageId, Integer progress, Integer numSteps) {
    if (messageId != null) {
      setMessage(getContext().getString(messageId));
    }

    if ((progress == null) || (numSteps == null) || (numSteps == 0)) {
      setIndeterminate(true);
    } else {
      setIndeterminate(false);
      setMax(numSteps);
      setProgress(progress);
    }
  }
}
