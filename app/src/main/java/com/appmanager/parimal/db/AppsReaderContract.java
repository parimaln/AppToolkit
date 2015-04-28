package com.appmanager.parimal.db;

import android.provider.BaseColumns;

/**
 * Created by parimal on 25-03-2015.
 */
public class AppsReaderContract {
    public AppsReaderContract() {
    }
    public static abstract class AppEntry implements BaseColumns {
        //----------------------------------------------------------
        //------------------Table Category DB----------------------------
        public static final String TABLE_NAME_CAT = "catstable";
        public static final String COLUMN_CAT_ID = "entryid";
        public static final String COLUMN_CAT_PIN = "catpin";
        public static final String COLUMN_CAT_PIN_USE = "catpinuse";
        public static final String COLUMN_CAT_USAGE = "catusage";
        //----------------------------------------------------------
        //------------------Table app DB----------------------------
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_APP_NAME = "title";
        public static final String COLUMN_APP_PACKAGE = "package";
        public static final String COLUMN_APP_CATEGORY = "category";
        public static final String COLUMN_APP_PIN = "apppin";
        public static final String COLUMN_APP_PIN_USE = "apppinuse";
        public static final String COLUMN_APP_USAGE = "appusage";
    }
}
