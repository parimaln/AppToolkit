package com.appmanager.parimal;

/**
 * Created by parimal on 28-04-2015.
 */
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.appmanager.parimal.db.AppEntryDBHelper;
import com.appmanager.parimal.db.AppsReaderContract;
import com.appmanager.parimal.model.AppDetail;

public class MyReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        AppEntryDBHelper mDbHelper = new AppEntryDBHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // when package removed
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            Log.e(" BroadcastReceiver ", "onReceive called "
                    + " PACKAGE_REMOVED "+getPackageName(intent));
            Toast.makeText(context, "PACKAGE_REMOVED! "+getPackageName(intent),
                    Toast.LENGTH_LONG).show();

            db.execSQL("DELETE FROM entry WHERE package='"+getPackageName(intent)+"'");
        }
        // when package installed
        else if (intent.getAction().equals(
                "android.intent.action.PACKAGE_ADDED")) {

            Log.e(" BroadcastReceiver ", "PACKAGE_ADDED");

            ApplicationInfo app = null;
            String label = null;
            try {
                app = context.getPackageManager().getApplicationInfo(getPackageName(intent), 0);
                label = app.loadLabel(context.getPackageManager()).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Toast.makeText(context, " PACKAGE_ADDED! "+label,
                    Toast.LENGTH_LONG).show();

            ContentValues values = new ContentValues();
            values.put(AppsReaderContract.AppEntry.COLUMN_APP_NAME, label);
            values.put(AppsReaderContract.AppEntry.COLUMN_APP_PACKAGE, getPackageName(intent));
            values.put(AppsReaderContract.AppEntry.COLUMN_APP_CATEGORY, "Uncategorized");
            values.put(AppsReaderContract.AppEntry.COLUMN_APP_PIN, "0000");
            values.put(AppsReaderContract.AppEntry.COLUMN_APP_PIN_USE, "false");
            values.put(AppsReaderContract.AppEntry.COLUMN_APP_USAGE, "0");
            db.insert(
                    AppsReaderContract.AppEntry.TABLE_NAME,
                    null,
                    values);

        }

        db.close();
    }

    String getPackageName(Intent intent) {
        Uri uri = intent.getData();
        String pkg = uri != null ? uri.getSchemeSpecificPart() : null;
        return pkg;
    }
}
