package com.appmanager.parimal.fragments;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appmanager.parimal.R;
import com.appmanager.parimal.activity.AddCatActivity;
import com.appmanager.parimal.activity.MainActivity;
import com.appmanager.parimal.adapter.CardsAppsAdapter;
import com.appmanager.parimal.db.AppEntryDBHelper;
import com.appmanager.parimal.db.AppsReaderContract;
import com.appmanager.parimal.model.AppDetail;

import java.util.ArrayList;
import java.util.List;


public class AppCategoryDetailFragment extends Fragment {

    public static final String APP_INDEX = "app_index";
    public static final String APP_NAME = "app_label";
    public static final String ARG_LV1 = "app_lv";
    public static List<AppDetail> allAppsList;
    private RecyclerView mRecyclerView=null;
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_CONTENT = "item_content";
    /**
     * The dummy content this fragment is presenting.
     */
    private boolean catType;
    private String catToLoad;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AppCategoryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appcategory_detail, container, false);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.list_cards);
        mRecyclerView.setHasFixedSize(false);
        setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 4));
        // Show the dummy content as text in a TextView.



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments().containsKey(ARG_ITEM_CONTENT)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            catToLoad = new String();
            catToLoad = getArguments().getString(ARG_ITEM_CONTENT);
        }else if(getArguments().containsKey(Intent.EXTRA_SHORTCUT_NAME)){
            catToLoad = new String();
            catToLoad = getArguments().getString(Intent.EXTRA_SHORTCUT_NAME);
        }
        loadApps();
        setAdapter(new CardsAppsAdapter(allAppsList, getActivity()));
    }

    private void loadApps() {
        boolean type;
        if(catToLoad.matches("All apps"))
            type = true;
        else
            type = false;
        boolean useCats;
        if(catToLoad.matches("Uncategorised")||catToLoad.matches("All apps"))
            useCats = false;
        else
            useCats = true;

        if(!useCats) {
            AppEntryDBHelper mDbHelper = new AppEntryDBHelper(getActivity().getApplicationContext());
            allAppsList = new ArrayList<AppDetail>();
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projection = {
                    AppsReaderContract.AppEntry._ID,
                    AppsReaderContract.AppEntry.COLUMN_APP_NAME,
                    AppsReaderContract.AppEntry.COLUMN_APP_PACKAGE,
                    AppsReaderContract.AppEntry.COLUMN_APP_CATEGORY
            };
            String sortOrder =
                    AppsReaderContract.AppEntry.COLUMN_APP_NAME + " ASC";
            Cursor c = null;
            if (type) {
                c = db.query(
                        AppsReaderContract.AppEntry.TABLE_NAME,                      // The table to query
                        projection,                                                  // The columns to return
                        null,                                     // The columns for the WHERE clause
                        null,                                     // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        sortOrder                                 // The sort order
                );
            } else {
                c = db.query(
                        AppsReaderContract.AppEntry.TABLE_NAME,                      // The table to query
                        projection,                                                  // The columns to return
                        AppsReaderContract.AppEntry.COLUMN_APP_CATEGORY + " = ?",    // The columns for the WHERE clause
                        new String[]{"Uncategorized"},                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        sortOrder                                 // The sort order
                );
            }


            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    AppDetail app = new AppDetail();

                    try {
                        ApplicationInfo _app = getActivity().getApplicationContext().getPackageManager().getApplicationInfo(c.getString(c.getColumnIndex(AppsReaderContract.AppEntry.COLUMN_APP_PACKAGE)), 0);
                        Drawable icon = getActivity().getPackageManager().getApplicationIcon(_app);
                        app.setId(c.getInt(c.getColumnIndex(AppsReaderContract.AppEntry._ID)));
                        app.setLabel(c.getString(c.getColumnIndex(AppsReaderContract.AppEntry.COLUMN_APP_NAME)));
                        app.setName(c.getString(c.getColumnIndex(AppsReaderContract.AppEntry.COLUMN_APP_PACKAGE)));
                        app.setIcon(icon);
                        allAppsList.add(app);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }


                } while (c.moveToNext());
            }
        }else{
            AppEntryDBHelper mDbHelper = new AppEntryDBHelper(getActivity().getApplicationContext());
            allAppsList = new ArrayList<AppDetail>();
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projection = {
                    AppsReaderContract.AppEntry._ID,
                    AppsReaderContract.AppEntry.COLUMN_APP_NAME,
                    AppsReaderContract.AppEntry.COLUMN_APP_PACKAGE,
                    AppsReaderContract.AppEntry.COLUMN_APP_CATEGORY
            };
            String sortOrder =
                    AppsReaderContract.AppEntry.COLUMN_APP_NAME + " ASC";
            Cursor c = null;
            c = db.query(
                    AppsReaderContract.AppEntry.TABLE_NAME,                      // The table to query
                    projection,                                                  // The columns to return
                    AppsReaderContract.AppEntry.COLUMN_APP_CATEGORY + " = ?",    // The columns for the WHERE clause
                    new String[]{catToLoad},                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            if (c.moveToFirst()) {
                do {
                    AppDetail app = new AppDetail();

                    try {
                        ApplicationInfo _app = getActivity().getApplicationContext().getPackageManager().getApplicationInfo(c.getString(c.getColumnIndex(AppsReaderContract.AppEntry.COLUMN_APP_PACKAGE)), 0);
                        Drawable icon = getActivity().getPackageManager().getApplicationIcon(_app);
                        app.setId(c.getInt(c.getColumnIndex(AppsReaderContract.AppEntry._ID)));
                        app.setLabel(c.getString(c.getColumnIndex(AppsReaderContract.AppEntry.COLUMN_APP_NAME)));
                        app.setName(c.getString(c.getColumnIndex(AppsReaderContract.AppEntry.COLUMN_APP_PACKAGE)));
                        app.setIcon(icon);
                        allAppsList.add(app);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }


                } while (c.moveToNext());
            }
        }
    }


    public void setLayoutManager(RecyclerView.LayoutManager mgr) {
        mRecyclerView.setLayoutManager(mgr);
    }
    public RecyclerView.Adapter getAdapter() {
        return(mRecyclerView.getAdapter());
    }
    public void setAdapter(CardsAppsAdapter adapter) {
        adapter.SetOnItemClickListener(new CardsAppsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String appLabel, String pkgInfo) {
                if (catToLoad.matches("Uncategorised")) {
                    Intent intent = new Intent(getActivity(), AddCatActivity.class);
                    intent.putExtra(APP_INDEX, position);
                    intent.putExtra(APP_NAME, appLabel);
                    startActivity(intent);

                } else {
                    Intent LaunchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(pkgInfo);
                    startActivity(LaunchIntent);
                }


            }

            @Override
            public void onItemLongClick(View view, final int position, final String appLabel, final String pkgInfo) {
                if(!((catToLoad.matches("Uncategorised"))||(catToLoad.matches("All apps")))){
                    String[] items = new String[2];
                    items[0] = "Edit Category";
                    items[1] = "Application Info";
                    new MaterialDialog.Builder(getActivity())
                            .items(items)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    if(which == 1){
                                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        i.addCategory(Intent.CATEGORY_DEFAULT);
                                        i.setData(Uri.parse("package:"+pkgInfo));
                                        startActivity(i);
                                    }else if(which == 0){
                                        Intent intent = new Intent(getActivity(), AddCatActivity.class);
                                        intent.putExtra(APP_INDEX, position);
                                        intent.putExtra(APP_NAME, appLabel);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .show();
                }
        }
    });
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.add_shortcut) {
            addShortcut();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    private void addShortcut() {
        //Adding shortcut for MainActivity
        //on Home screen
        Intent shortcutIntent = new Intent(getActivity().getApplicationContext(),
                MainActivity.class);
        shortcutIntent.putExtra(ARG_ITEM_CONTENT, catToLoad);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, catToLoad);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getActivity().getApplicationContext(),
                        R.mipmap.ic_launcher));

        addIntent
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getActivity().getApplicationContext().sendBroadcast(addIntent);
    }
}
