package com.zvelo.walletcracker;

import com.google.android.apps.analytics.easytracking.TrackedActivity;
import com.viewpagerindicator.TitlePageIndicator;
import com.zvelo.walletcracker.BGLoader.Progress;
import com.zvelo.walletcracker.BGLoader.Status;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class WalletCrackerMain extends TrackedActivity implements WalletListener {
  protected final String TAG = this.getClass().getSimpleName();
  protected final String RESTORE = ", can restore state";
  static private Boolean _progressLock = false;
  static private ProgressDialogFragment _progress;


  public static class ProgressDialogFragment extends DialogFragment {
    static public final String TAG = "ProgressDialogFragment";

    public static ProgressDialogFragment newInstance(Integer messageId, Integer progress, Integer numSteps) {
      ProgressDialogFragment fragment = new ProgressDialogFragment();

      Bundle args = new Bundle();
      args.putInt("messageId", messageId);
      args.putInt("progress", progress);
      args.putInt("numSteps", numSteps);
      fragment.setArguments(args);

      return fragment;
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
      final Integer messageId = getArguments().getInt("messageId");
      final Integer progress = getArguments().getInt("progress");
      final Integer numSteps = getArguments().getInt("numSteps");

      ZveloProgressDialog dialog = new ZveloProgressDialog(getActivity());

      dialog.setTitle(R.string.loading);
      dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      dialog.setCancelable(false);
      dialog.update(messageId, progress, numSteps);

      return (Dialog) dialog;
    }

    public void update(Integer messageId, Integer progress, Integer numSteps) {
      ZveloProgressDialog dialog = (ZveloProgressDialog) getDialog();
      if (dialog == null) {
        return;
      }
      dialog.update(messageId, progress, numSteps);
    }
  }

  static public class ZveloProgressDialog extends ProgressDialog {
    public ZveloProgressDialog(Context context) {
      super(context);
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

  static public class ErrorDialogFragment extends DialogFragment {
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
                  ((WalletCrackerMain) getActivity()).doErrorClick();
                }
              })
              .create();
    }
  }

  public void doErrorClick() {
    Log.i(TAG, "Error Clicked");
    this.finish();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pager);

    getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

    ViewPagerAdapter adapter = new ViewPagerAdapter(this);
    adapter.addTab(R.string.pin,   PinFragment.class,   null);
    adapter.addTab(R.string.data,  DataFragment.class,  null);
    adapter.addTab(R.string.about, AboutFragment.class, null);

    ViewPager pager = (ViewPager) findViewById(R.id.pager);
    TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.titles);
    pager.setAdapter(adapter);
    indicator.setViewPager(pager);

    rebuild(false);

    // savedInstanceState could be null
    if (savedInstanceState != null) {
      // TODO
    }
  }

  @Override protected void onRestart() {
    super.onRestart();
    Log.i(TAG, "onRestart");
  }

  @Override protected void onStart() {
    super.onStart();
    Log.i(TAG, "onStart");
  }

  @Override protected void onResume() {
    super.onResume();
    Log.i(TAG, "onResume");
    BGLoader.addListener(this);
  }

  @Override protected void onPause() {
    super.onPause();
    Log.i(TAG, "onPause" + (isFinishing() ? "Finishing" : ""));
    BGLoader.removeListener(this);
  }

  @Override protected void onStop() {
    super.onStop();
    Log.i(TAG, "onStop");
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "onDestroy" + Integer.toString(getChangingConfigurations(), 16));
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    // TODO
    Log.i(TAG, "onSaveInstanceState");
  }

  @Override protected void onRestoreInstanceState(Bundle savedState) {
    super.onRestoreInstanceState(savedState);
    Object oldTaskObject = getLastNonConfigurationInstance();
    if (oldTaskObject != null) {
      int oldTask = ((Integer) oldTaskObject).intValue();
      int currentTask = getTaskId();
      assert oldTask == currentTask;
    }
    Log.i(TAG, "onRestoreInstanceState" + (null == savedState ? "" : RESTORE));
  }

  @Override protected void onPostCreate(Bundle savedState) {
    super.onPostCreate(savedState);
    if (savedState != null ) {
      // TODO
    }
    Log.i(TAG, "onPostCreate" + (savedState == null ? "" : RESTORE));
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    Log.i(TAG, "onPostResume");
  }

  @Override protected void onUserLeaveHint() {
    super.onUserLeaveHint();
    Log.i(TAG, "onUserLeaveHint");
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    final MenuInflater inflater = getMenuInflater();
    Log.i(TAG, "onCreateOptionsMenu");
    inflater.inflate(R.menu.mainmenu, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
    Log.i(TAG, "onOptionsItemSelected");
    switch (item.getItemId()) {
      case R.id.rebuild:
        Log.i(TAG, "user asked for rebuild");
        rebuild(true);
        break;
      case R.id.prefs:
        Log.i(TAG, "user asked for prefs");
        startActivity(new Intent(this, PreferencesMain.class));
        break;
    }
    return true;
  }

  private void rebuild(Boolean force) {
    Log.i(TAG, "rebuild, force: "+force);
    new BGLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this, force);
  }

  private void showError(int messageId) {
    DialogFragment fragment = ErrorDialogFragment.newInstance(messageId);
    fragment.show(getFragmentManager(), "error");
  }

  private void showProgress(Integer messageId, Integer progress, Integer numSteps) {
    synchronized(_progressLock) {
      if (_progressLock) {
        Log.d(TAG, "updating existing progress dialog");
        _progress.update(messageId, progress, numSteps);
        return;
      }

      _progressLock = true;

      Log.d(TAG, "creating new progress dialog");
      _progress = ProgressDialogFragment.newInstance(messageId, progress, numSteps);
      _progress.show(getFragmentManager(), ProgressDialogFragment.TAG);
    }
  }

  private void hideDialog(String tag) {
    synchronized(_progressLock) {
      if (_progressLock) {
        Log.d(TAG, "dismissing "+tag);
        _progress.dismiss();
        _progressLock = false;
      }
    }
  }

  @Override  public void walletLoaded(Status result, DeviceInfoParser parser) {
    hideDialog(ProgressDialogFragment.TAG);

    switch (result) {
      case NO_WALLET:
        showError(R.string.wallet_not_found);
        break;
      case NO_ROOT:
        showError(R.string.root_not_found);
        break;
    }
  }

  @Override public void walletProgress(Progress progress, Integer numSteps) {
    int stringId = R.string.loading;

    switch (progress) {
      case WALLET:
        stringId = R.string.loading_wallet;
        break;
      case ROOT:
        stringId = R.string.loading_root;
        break;
      case COPYING:
        stringId = R.string.loading_copying;
        break;
      case CRACKING:
        stringId = R.string.loading_cracking;
        break;
    }

    showProgress(stringId, ((progress == null) ? null : progress.ordinal()), numSteps);
  }
}