package com.zvelo.walletcracker;

import com.viewpagerindicator.TitlePageIndicator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class WalletCrackerMain extends Activity {
  protected final String TAG = this.getClass().getSimpleName();
  protected final String RESTORE = ", can restore state";

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
    Log.i(TAG, "rebuild force: "+force);
    new BGLoader().execute(this, force);
  }
}