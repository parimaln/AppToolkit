package com.appmanager.parimal.activity;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.appmanager.parimal.R;
import com.appmanager.parimal.db.AppEntryDBHelper;
import com.appmanager.parimal.db.AppsReaderContract;
import com.appmanager.parimal.fragments.AppCategoryDetailFragment;
import com.appmanager.parimal.fragments.FragmentDrawer;
import com.appmanager.parimal.model.AppDetail;

import java.util.ArrayList;
import java.util.List;

import de.psdev.licensesdialog.LicensesDialogFragment;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    SharedPreferences prefs = null;
    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private PackageManager manager;
    private List<AppDetail> apps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppsAsyncTask(true);


        if(getIntent().hasExtra(AppCategoryDetailFragment.ARG_ITEM_CONTENT)){
            displayView(getIntent().getStringExtra(AppCategoryDetailFragment.ARG_ITEM_CONTENT), 1);
        }else
            displayView("Uncategorised", 1);
        drawerFragment.openDrawer();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main_all, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_opensource) {

            final Notices notices = new Notices();
            notices.addNotice(new Notice("Material Dialogs", "https://github.com/afollestad/material-dialogs", "Copyright (c) 2015 Aidan Michael Follestad", new MITLicense()));
            final LicensesDialogFragment fragment = LicensesDialogFragment.newInstance(notices, false, true);
            fragment.show(getSupportFragmentManager(), null);
            return true;
        }else if(id == R.id.action_force_refresh){
            this.deleteDatabase("AppReader.db");
            loadAppsAsyncTask(false);
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        TextView mView = (TextView) view.findViewById(R.id.title);
        displayView(mView.getText().toString(), position);
    }

    private void displayView(String viewLabel, int position) {

        Fragment fragment = null;
        String title = new String();

        fragment = new AppCategoryDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putString(AppCategoryDetailFragment.ARG_ITEM_CONTENT, viewLabel);
        fragment.setArguments(arguments);
        title = viewLabel;


        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }



    private void loadApps() {

        manager = this.getPackageManager();
        apps = new ArrayList<AppDetail>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        AppEntryDBHelper mDbHelper = new AppEntryDBHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            if(!ri.activityInfo.packageName.matches("com.appmanager.parimal")){
                AppDetail app = new AppDetail();
                app.setLabel(ri.loadLabel(manager));
                app.setName(ri.activityInfo.packageName);
                app.setIcon(ri.activityInfo.loadIcon(manager));
                apps.add(app);
                ContentValues values = new ContentValues();
                values.put(AppsReaderContract.AppEntry.COLUMN_APP_NAME, ri.loadLabel(manager).toString());
                values.put(AppsReaderContract.AppEntry.COLUMN_APP_PACKAGE, ri.activityInfo.packageName);
                values.put(AppsReaderContract.AppEntry.COLUMN_APP_CATEGORY, "Uncategorized");
                values.put(AppsReaderContract.AppEntry.COLUMN_APP_PIN, "0000");
                values.put(AppsReaderContract.AppEntry.COLUMN_APP_PIN_USE, "false");
                values.put(AppsReaderContract.AppEntry.COLUMN_APP_USAGE, "0");


                long newRowId;
                newRowId = db.insert(
                        AppsReaderContract.AppEntry.TABLE_NAME,
                        null,
                        values);
            }

        }
        db.close();
    }

    private void loadAppsAsyncTask(boolean firstTimeLoad) {
        AsyncTask<Void, Void, Void> appsTask = new AsyncTask<Void, Void, Void>() {
            MaterialDialog show;
            @Override
            protected Void doInBackground(Void... params) {
                loadApps();
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                show = new MaterialDialog.Builder(MainActivity.this)
                        .title("Please Wait!")
                        .content("Fetching all your cats")
                        .theme(Theme.LIGHT)
                        .cancelable(false)
                        .progress(true, 0)
                        .show();
                Log.d("TAG", "Fetching apps");
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                show.dismiss();
                displayView("Uncategorised", 1);
            }
        };
        if(firstTimeLoad){
            prefs = getSharedPreferences("com.appmanager.parimal", MODE_PRIVATE);
            if (prefs.getBoolean("firstrun", true)) {
                // Do first run stuff here then set 'firstrun' as false
                // using the following line to edit/commit prefs
                prefs.edit().putBoolean("firstrun", false).commit();

                appsTask.execute();
            }
        }else
            appsTask.execute();

    }
}
