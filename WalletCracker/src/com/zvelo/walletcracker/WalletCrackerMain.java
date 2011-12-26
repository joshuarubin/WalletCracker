package com.zvelo.walletcracker;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

public class WalletCrackerMain extends Activity {
  protected final String TAG = this.getClass().getSimpleName();
  protected final String RESTORE = ", can restore state";
  protected ViewPager _pager;
  protected TabsAdapter _tabsAdapter;

  public static class TabsAdapter
    extends FragmentPagerAdapter
    implements ViewPager.OnPageChangeListener, ActionBar.TabListener
  {
    private final Context _context;
    private final ActionBar _actionBar;
    private final ViewPager _viewPager;
    private final ArrayList<TabInfo> _tabs = new ArrayList<TabInfo>();

    static final class TabInfo {
      private final Class<?> _class;
      private final Bundle _args;
  
      TabInfo(Class<?> clss, Bundle args) {
        _class = clss;
        _args = args;
      }
    }

    public TabsAdapter(Activity activity, ViewPager pager) {
      super(activity.getFragmentManager());
      _context = activity;
      _actionBar = activity.getActionBar();
      _viewPager = pager;
      _viewPager.setAdapter(this);
      _viewPager.setOnPageChangeListener(this);
    }

    public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
      TabInfo info = new TabInfo(clss, args);
      tab.setTag(info);
      tab.setTabListener(this);
      _tabs.add(info);
      _actionBar.addTab(tab);
      notifyDataSetChanged();
    }

    @Override public int getCount() {
      return _tabs.size();
    }

    @Override public Fragment getItem(int position) {
      TabInfo info = _tabs.get(position);
      return Fragment.instantiate(_context, info._class.getName(), info._args);
    }

    @Override public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override public void onTabSelected(Tab tab, FragmentTransaction ft) {
      Object tag = tab.getTag();
      for (int i=0; i < _tabs.size(); i++) {
        if (_tabs.get(i) == tag) {
          _viewPager.setCurrentItem(i);
        }
      }
    }

    @Override public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override public void onPageScrollStateChanged(int arg0) {
    }

    @Override public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override public void onPageSelected(int position) {
      _actionBar.setSelectedNavigationItem(position);
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    _pager = new ViewPager(this);
    _pager.setId(R.id.pager);
    setContentView(_pager);

    final ActionBar actionBar = getActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    //actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
    actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

    _tabsAdapter = new TabsAdapter(this, _pager);
    _tabsAdapter.addTab(actionBar.newTab().setText(R.string.data), DataFragment.class, null);
    _tabsAdapter.addTab(actionBar.newTab().setText(R.string.about), AboutFragment.class, null);

    // savedInstanceState could be null
    if (savedInstanceState != null) {
      actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
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
    outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
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
}