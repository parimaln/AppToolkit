package com.appmanager.parimal.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by parimal on 25-03-2015.
 */
public class AppEntryDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AppReader.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AppsReaderContract.AppEntry.TABLE_NAME + " (" +
                    AppsReaderContract.AppEntry._ID + " INTEGER PRIMARY KEY," +
                    AppsReaderContract.AppEntry.COLUMN_APP_NAME + TEXT_TYPE + COMMA_SEP +
                    AppsReaderContract.AppEntry.COLUMN_APP_PACKAGE + TEXT_TYPE + COMMA_SEP +
                    AppsReaderContract.AppEntry.COLUMN_APP_PIN + TEXT_TYPE + COMMA_SEP +
                    AppsReaderContract.AppEntry.COLUMN_APP_PIN_USE + TEXT_TYPE + COMMA_SEP +
                    AppsReaderContract.AppEntry.COLUMN_APP_USAGE + TEXT_TYPE + COMMA_SEP +
                    AppsReaderContract.AppEntry.COLUMN_APP_CATEGORY + TEXT_TYPE +
            " )";

    /*private static final String SQL_CREATE_CATS =
            "CREATE TABLE " + AppsReaderContract.AppEntry.TABLE_NAME_CAT + " (" +
                    AppsReaderContract.AppEntry._ID + " INTEGER PRIMARY KEY," +
                    AppsReaderContract.AppEntry.COLUMN_CAT_PIN + TEXT_TYPE + COMMA_SEP +
                    AppsReaderContract.AppEntry.COLUMN_CAT_USAGE + TEXT_TYPE + COMMA_SEP +
                    AppsReaderContract.AppEntry.COLUMN_CAT_ID + TEXT_TYPE + COMMA_SEP +
                    AppsReaderContract.AppEntry.COLUMN_CAT_PIN_USE + TEXT_TYPE +
                    " )";*/
    private static final String SQL_DELETE_ENTRIES ="DROP TABLE IF EXISTS " + AppsReaderContract.AppEntry.TABLE_NAME;
    //private static final String SQL_DELETE_CATS ="DROP TABLE IF EXISTS " + AppsReaderContract.AppEntry.TABLE_NAME_CAT;
    public AppEntryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        //db.execSQL(SQL_CREATE_CATS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        //db.execSQL(SQL_DELETE_CATS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
