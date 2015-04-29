package com.appmanager.parimal.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.appmanager.parimal.R;
import com.appmanager.parimal.adapter.NavigationDrawerAdapter;
import com.appmanager.parimal.db.AppEntryDBHelper;
import com.appmanager.parimal.db.AppsReaderContract;
import com.appmanager.parimal.fragments.AppCategoryDetailFragment;
import com.appmanager.parimal.fragments.FragmentDrawer;
import com.appmanager.parimal.model.AppDetail;
import com.appmanager.parimal.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;


public class AddCatActivity extends AppCompatActivity {
    private static int app_index = 0;
    AppDetail currentApp = null;
    TextView label;
    EditText catEntry;
    int numRows = 0;
    private RecyclerView recyclerView;
    private NavigationDrawerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cat);
        recyclerView = (RecyclerView) findViewById(R.id.suggestionList);
        numRows = loadListSuggest();
        label = (TextView) findViewById(R.id.newCatHeader);
        catEntry = (EditText) findViewById(R.id.editTextNewCat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
        Intent intent = getIntent();
        app_index = intent.getIntExtra(AppCategoryDetailFragment.APP_INDEX, 0);
        label.setText("Enter the category for "+intent.getStringExtra(AppCategoryDetailFragment.APP_NAME));
        //currentApp = AppCategoryDetailFragment.allAppsList.get(app_index);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(numRows>0)
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private int loadListSuggest() {
        Cursor c = null;
        String[] projection = {
                AppsReaderContract.AppEntry._ID,
                AppsReaderContract.AppEntry.COLUMN_APP_CATEGORY
        };
        String sortOrder =
                AppsReaderContract.AppEntry.COLUMN_APP_CATEGORY + " ASC";
        AppEntryDBHelper mDbHelper = new AppEntryDBHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        c = db.query(
                AppsReaderContract.AppEntry.TABLE_NAME,                      // The table to query
                projection,                                                  // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        final ArrayList<String> items = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                String _category;
                _category = c.getString(c.getColumnIndex(AppsReaderContract.AppEntry.COLUMN_APP_CATEGORY));
                if(!_category.matches("Uncategorized"))
                    if(!items.contains(_category))
                        items.add(_category);

            } while (c.moveToNext());
        }
        int x = items.size();
        String[] titles = new String[x];
        titles = items.toArray(titles);
        adapter = new NavigationDrawerAdapter(this, getData(titles));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final String[] finalTitles = titles;
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new FragmentDrawer.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                catEntry.setText(finalTitles[position]);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        db.close();
        return items.size();
    }

    private List<NavDrawerItem> getData(String[] titles) {
        List<NavDrawerItem> data = new ArrayList<>();


        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles[i]);
            data.add(navItem);
        }
        return data;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_cat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                setCategory();

        }
        return super.onOptionsItemSelected(item);
    }

    private void setCategory() {
        AppEntryDBHelper mDbHelper = new AppEntryDBHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AppsReaderContract.AppEntry.COLUMN_APP_CATEGORY, catEntry.getText().toString());

        // updating row
        db.update(AppsReaderContract.AppEntry.TABLE_NAME, values, AppsReaderContract.AppEntry._ID + " = ?",
                new String[] { String.valueOf(app_index) });
        finish();
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private FragmentDrawer.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FragmentDrawer.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {


                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }
    }
}
