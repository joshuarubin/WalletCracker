package com.zvelo.walletcracker;

import com.viewpagerindicator.TitlePageIndicator;
import com.zvelo.walletcracker.BGLoader.Progress;
import com.zvelo.walletcracker.BGLoader.Status;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class WalletCrackerMain extends Activity implements WalletListener {
  protected final String TAG = this.getClass().getSimpleName();
  protected final String RESTORE = ", can restore state";
  protected ProgressDialog _progress;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pager);

    _progress = new ProgressDialog(this);
    _progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    _progress.setCancelable(false);

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
  }

  @Override protected void onPause() {
    super.onPause();
    Log.i(TAG, "onPause" + (isFinishing() ? "Finishing" : ""));
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
    new BGLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this, this, force);
  }

  public void showError(int stringId) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(stringId)
           .setCancelable(false)
           .setNeutralButton("OK", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                WalletCrackerMain.this.finish();
              }
          })
          .show();
  }

  private void showProgress(Integer stringId, Integer progress, Integer numSteps) {
    if (stringId != null) {
      _progress.setMessage(getString(stringId));
    }

    if ((progress == null) || (numSteps == null) || (numSteps == 0)) {
      _progress.setIndeterminate(true);
    } else {
      _progress.setIndeterminate(false);
      _progress.setMax(numSteps);
      _progress.setProgress(progress);
    }

    _progress.show();
  }

  private void hideProgress() {
    _progress.hide();
  }

  @Override  public void walletLoaded(Status result, DeviceInfoParser parser) {
    hideProgress();

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