package com.zvelo.walletcracker;

import java.util.ArrayList;

import com.viewpagerindicator.TitleProvider;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter implements TitleProvider {
  private final Context _context;
  private final ArrayList<TabInfo> _tabs = new ArrayList<TabInfo>();

  final class TabInfo {
    private final String _title;
    private final Class<?> _class;
    private final Bundle _args;

    TabInfo(Integer titleId, Class<?> clss, Bundle args) {
      _title = _context.getString(titleId);
      _class = clss;
      _args = args;
    }
  }

  public ViewPagerAdapter(Activity activity) {
    super(activity.getFragmentManager());
    _context = activity;
  }

  public void addTab(int titleId, Class<?> clss, Bundle args) {
    TabInfo info = new TabInfo(titleId, clss, args);
    _tabs.add(info);
  }

  @Override public int getCount() {
    return _tabs.size();
  }

  @Override public Fragment getItem(int position) {
    TabInfo info = _tabs.get(position);
    return Fragment.instantiate(_context, info._class.getName(), info._args);
  }

  @Override public String getTitle(int position) {
    return _tabs.get(position)._title;
  }
}