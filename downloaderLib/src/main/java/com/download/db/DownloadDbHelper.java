/*
 * Copyright (C) loongmobile.com, Inc. All Rights Reserved.
 * website: www.loongmobile.com
 */

package com.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//import com.loongmobile.ads.csdata.AdsLog;

/**
 * This class is the Application information content provider.
 */
public class DownloadDbHelper extends SQLiteOpenHelper {
    private static final String LOGTAG = "DownloadDbHelper";

    public DownloadDbHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DownloadDbHelper(Context context, String name, CursorFactory factory, int version, boolean isSysDb) {
        this(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        Log.i("DownloadDbHelper", "onCreate");
        db.execSQL("CREATE TABLE " + DownloadDbInfo.TABLE_NAME + " (" + DownloadDbInfo._ID + " INTEGER PRIMARY KEY,"
                + DownloadDbInfo.COLUMN_ID + " TEXT, "
                + DownloadDbInfo.COLUMN_TITLE + " TEXT, "
                + DownloadDbInfo.COLUMN_URL + " TEXT, "
                + DownloadDbInfo.COLUMN_TYPE + " int(1), "
                + DownloadDbInfo.COLUMN_IS_MOD + " int(1), "
                + DownloadDbInfo.COLUMN_SIZE + " INTEGER, "
                + DownloadDbInfo.COLUMN_DOWNLOAD_SIZE + " INTEGER, "
                + DownloadDbInfo.COLUMN_DOWNLOAD_POSITION + " TEXT, "
                + DownloadDbInfo.COLUMN_EXTRA + " TEXT, "
                + DownloadDbInfo.COLUMN_STATUS + " INTEGER, "
                + DownloadDbInfo.COLUMN_CREATE_TIME + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        AdsLog.w(LOGTAG, "Upgrading database from version " + oldVersion + " to " + newVersion
//                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DownloadDbInfo.TABLE_NAME);
        onCreate(db);
    }

    ;

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        AdsLog.w(LOGTAG, "Upgrading database from version " + oldVersion + " to " + newVersion
//                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DownloadDbInfo.TABLE_NAME);
        onCreate(db);
    }
}
