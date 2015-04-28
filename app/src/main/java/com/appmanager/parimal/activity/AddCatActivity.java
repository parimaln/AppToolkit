package com.appmanager.parimal.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.appmanager.parimal.R;
import com.appmanager.parimal.db.AppEntryDBHelper;
import com.appmanager.parimal.db.AppsReaderContract;
import com.appmanager.parimal.fragments.AppCategoryDetailFragment;
import com.appmanager.parimal.model.AppDetail;

import java.util.ArrayList;


public class AddCatActivity extends AppCompatActivity {
    private static int app_index = 0;
    AppDetail currentApp = null;
    TextView label;
    EditText catEntry;
    ListView lv2;
    int numRows = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cat);
        lv2 = (ListView) findViewById(R.id.listViewSuggest);
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

        lv2.setAdapter(new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                items));
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                catEntry.setText(items.get(position));
            }
        });
        return items.size();
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
}
