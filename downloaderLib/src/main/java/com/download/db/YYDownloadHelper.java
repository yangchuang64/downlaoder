/*
 * Copyright (C) loongmobile.com, Inc. All Rights Reserved.
 * website: www.loongmobile.com
 */

package com.download.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.download.task.DownloadTaskInfo;
import com.download.constants.DownloadConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides convenient functions to visit database.
 */
public class YYDownloadHelper {
    private static DownloadDbHelper mOpenHelper;

    private static DownloadDbHelper getDatabase(Context context) {
        if (mOpenHelper == null)
            mOpenHelper = new DownloadDbHelper(context, DownloadDbInfo.DATABASE_NAME, null, DownloadDbInfo.DATABASE_VERSION);
        return mOpenHelper;
    }

    /**
     * Get a cursor which points to a application with download id.
     *
     * @param context
     * @return
     */
    public static DownloadTaskInfo getInfoByAppDownloadId(Context context, String downloadId) {
        if (context == null || downloadId == null)
            return null;

        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
        Cursor cursor = db.query(DownloadDbInfo.TABLE_NAME, null, DownloadDbInfo.COLUMN_ID + "='" + downloadId + "'", null, null, null, null);
        return createDownloadInfo(context, cursor);
    }

//    /**
//     * Get a cursor which points to a application with url.
//     *
//     * @param context
//     * @return
//     */
//    public static YYDownloadInfo getInfoByAppUrl(Context context, String url) {
//        if (context == null || TextUtils.isEmpty(url))
//            return null;
//
//        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
//        Cursor cursor = db.query(DownloadDbInfo.TABLE_NAME, null, DownloadDbInfo.COLUMN_URL + "='" + url + "'", null, null, null, null);
//        return createDownloadInfo(context, cursor);
//    }

    public static List<DownloadTaskInfo> getInfoByAppType(Context context, int type) {
        if (context == null)
            return null;

        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
        Cursor cursor = db.query(DownloadDbInfo.TABLE_NAME, null, DownloadDbInfo.COLUMN_TYPE + "=" + type + "", null, null, null, null);
        return createDownloadInfos(context, cursor);
    }

    public static List<DownloadTaskInfo> getDownloadededTaskByAppType(Context context, int type) {
        if (context == null)
            return null;

        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
        Cursor cursor = db.query(DownloadDbInfo.TABLE_NAME, null, DownloadDbInfo.COLUMN_TYPE + " = ? AND " + DownloadDbInfo.COLUMN_STATUS + " = ?",
                new String[]{String.valueOf(type), String.valueOf(DownloadConstant.STATUS_COMPLETE)}, null, null, null);
        return createDownloadInfos(context, cursor);
    }

//    public static YYDownloadInfo getInfoByAppMod(Context context, int isMod) {
//        if (context == null)
//            return null;
//
//        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
//        Cursor cursor = db.query(DownloadDbInfo.TABLE_NAME, null, DownloadDbInfo.COLUMN_IS_MOD + "=" + isMod + "", null, null, null, null);
//        return createDownloadInfo(context, cursor);
//    }

    /**
     * Get a cursor which points to the application with status.
     *
     * @param context
     * @param status
     * @return
     */
    public static DownloadTaskInfo getAppByStatus(Context context, int status) {
        if (context == null)
            return null;
        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
        Cursor cursor = db.query(DownloadDbInfo.TABLE_NAME, null,
                DownloadDbInfo.COLUMN_STATUS + " in(" + status + ")", null, null, null, null);

        return createDownloadInfo(context, cursor);
    }

    /**
     * 获取所有下载信息
     *
     * @param context
     * @param sortOrder
     * @return
     */
    public static List<DownloadTaskInfo> getAllDownloadInfo(Context context, String sortOrder) {
        if (context == null)
            return null;
        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
        Cursor cursor = db.query(DownloadDbInfo.TABLE_NAME, null, null, null, null, null, DownloadDbInfo._ID + " DESC");
        return createDownloadInfos(context, cursor);
    }

    /**
     * Get a cursor which points to the paused applications.
     *
     * @param context
     * @return
     */
    public static List<DownloadTaskInfo> getPausedInfo(Context context) {
        if (context == null)
            return null;
        List<DownloadTaskInfo> infos = new ArrayList();
        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
        Cursor cursor = db.query(DownloadDbInfo.TABLE_NAME, null,
                DownloadDbInfo.COLUMN_STATUS + " in(" + DownloadConstant.STATUS_PAUSED + ")", null, null, null, null);
        return createDownloadInfos(context, cursor);
    }

    private static DownloadTaskInfo createDownloadInfo(Context context, Cursor cursor) {
        List<DownloadTaskInfo> infos = createDownloadInfos(context, cursor);
        if (infos.size() > 0)
            return infos.get(0);
        return null;
    }

    /**
     * 返回一个下载列表,会删除无效的下载
     *
     * @param cursor
     * @return
     */
    private static List<DownloadTaskInfo> createDownloadInfos(Context context, Cursor cursor) {
        List<DownloadTaskInfo> infos = new ArrayList();
        List<String> invalidateInfos = new ArrayList();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        DownloadTaskInfo info = new DownloadTaskInfo();
                        info.setId(cursor.getInt(cursor.getColumnIndex(DownloadDbInfo._ID)));
                        info.setDownloadId(cursor.getString(cursor.getColumnIndex(DownloadDbInfo.COLUMN_ID)));
                        info.setTitle(cursor.getString(cursor.getColumnIndex(DownloadDbInfo.COLUMN_TITLE)));
                        info.setUrl(cursor.getString(cursor.getColumnIndex(DownloadDbInfo.COLUMN_URL)));
                        info.setType(cursor.getInt(cursor.getColumnIndex(DownloadDbInfo.COLUMN_TYPE)));
//                        info.isMod = cursor.getInt(cursor.getColumnIndex(DownloadDbInfo.COLUMN_IS_MOD));
                        info.setSize(cursor.getLong(cursor.getColumnIndex(DownloadDbInfo.COLUMN_SIZE)));
                        info.setDownloadSize(cursor.getLong(cursor.getColumnIndex(DownloadDbInfo.COLUMN_DOWNLOAD_SIZE)));
                        info.setFilePath(cursor.getString(cursor.getColumnIndex(DownloadDbInfo.COLUMN_DOWNLOAD_POSITION)));
                        info.setExtra(cursor.getString(cursor.getColumnIndex(DownloadDbInfo.COLUMN_EXTRA)));
                        info.setStatus(cursor.getInt(cursor.getColumnIndex(DownloadDbInfo.COLUMN_STATUS)));

                        File file = new File(info.getFilePath());
                        if (info.getDownloadSize() > 0 && !file.exists()) {
                            // 下载文件失效，需要清理数据库
                            invalidateInfos.add(info.getDownloadId());
                            if (file.exists())
                                file.delete();
                            continue;
                        }
                        infos.add(info);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // 纠正数据
        if (invalidateInfos.size() > 0) {
            StringBuffer strBuffer = new StringBuffer();
            for (int i = 0; i < invalidateInfos.size(); i++) {
                strBuffer.append("'").append(invalidateInfos.get(i)).append("'");
                if (i != invalidateInfos.size() - 1)
                    strBuffer.append(",");
            }
            getDatabase(context).getWritableDatabase().delete(DownloadDbInfo.TABLE_NAME, DownloadDbInfo.COLUMN_ID + " in (" + strBuffer.toString() + ")", null);
        }
        return infos;
    }

    public static boolean checkAppExsit(Context context, String downloadId) {
        if (context == null || downloadId == null)
            return true;

        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
        Cursor cursor = db.query(DownloadDbInfo.TABLE_NAME, null, DownloadDbInfo.COLUMN_ID + "='" + downloadId + "'", null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                return true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return false;
    }

    /**
     * Clear all content
     *
     * @param context
     * @return
     */
    public static int deleteAll(Context context) {
        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
        return db.delete(DownloadDbInfo.TABLE_NAME, null, null);
    }

    /**
     * Inserts a application row into the database.
     */
    public static long insertApp(Context context, String downloadId, int type, String title, String url, String filePath, String extra) {
        if (context == null || downloadId == null)
            return -1;

        if (checkAppExsit(context, downloadId)) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(DownloadDbInfo.COLUMN_ID, downloadId);
        values.put(DownloadDbInfo.COLUMN_TYPE, type);
        values.put(DownloadDbInfo.COLUMN_TITLE, title);
        values.put(DownloadDbInfo.COLUMN_URL, url);
        values.put(DownloadDbInfo.COLUMN_SIZE, -1);
        values.put(DownloadDbInfo.COLUMN_DOWNLOAD_SIZE, 0);
        values.put(DownloadDbInfo.COLUMN_DOWNLOAD_POSITION, filePath);
        values.put(DownloadDbInfo.COLUMN_EXTRA, extra);
        values.put(DownloadDbInfo.COLUMN_STATUS, DownloadConstant.STATUS_READY);
        values.put(DownloadDbInfo.COLUMN_CREATE_TIME, System.currentTimeMillis());

        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
        return db.insert(DownloadDbInfo.TABLE_NAME, null, values);
    }

    /**
     * Update application row in database by application id.
     *
     * @param context
     * @param downloadId
     * @param values
     * @return
     */
    public static int updateInfoByUrl(Context context, String downloadId, ContentValues values) {
        if (context == null || downloadId == null || values == null)
            return -1;

        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
        return db.update(DownloadDbInfo.TABLE_NAME, values, DownloadDbInfo.COLUMN_ID + "='" + downloadId + "'", null);
    }

    /**
     * Update application's status in database by application id.
     *
     * @param context
     * @param status
     * @return
     */
    public static int updateInfoStatus(Context context, String downloadId, int status) {
        if (context == null || downloadId == null)
            return -1;

        if (status < DownloadConstant.STATUS_READY || status > DownloadConstant.STATUS_ERROR) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(DownloadDbInfo.COLUMN_STATUS, status);
        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
        return db.update(DownloadDbInfo.TABLE_NAME, values, DownloadDbInfo.COLUMN_ID + "='" + downloadId + "'", null);
    }

    public static int deleteInfo(Context context, String downloadId) {
//        Log.i("YYDownloadHelper", "deleteInfo");
        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
        return db.delete(DownloadDbInfo.TABLE_NAME, DownloadDbInfo.COLUMN_ID + "='" + downloadId + "'", null);
    }

    /**
     * Set the downloading and upgrading application's status to
     * AppInfo.APP_STATUS_PAUSE.
     */
    public static void correctInfoStatus(Context context) {
        // delete downloading tasks
        SQLiteDatabase db = getDatabase(context).getWritableDatabase();
//        Cursor cursor = db.query(DownloadDbInfo.TABLE_NAME, null, DownloadDbInfo.COLUMN_STATUS + " in(" + DownloadConstant.STATUS_READY + "," + DownloadConstant.STATUS_START + "," + DownloadConstant.STATUS_DOWNLOADING + ")", null, null, null, null);
//        if (cursor != null) {
//            Log.i("DownloadHelper", "cout:" + cursor.getCount());
//            if (cursor.getCount() > 0) {
//                if (cursor.moveToFirst()) {
//                    do {
//                        int downloadId = cursor.getInt(cursor.getColumnIndex(DownloadDbInfo.COLUMN_ID));
//                        String filePath = cursor.getString(cursor.getColumnIndex(DownloadDbInfo.COLUMN_DOWNLOAD_POSITION));
//                        db.delete(DownloadDbInfo.TABLE_NAME, DownloadDbInfo.COLUMN_ID + "=" + downloadId + "", null);
//                        FileUtils.deleteFile(filePath);
//                    } while (cursor.moveToNext());
//                }
//            }
//            cursor.close();
//            cursor = null;
//        }
        ContentValues values = new ContentValues();
        values.put(DownloadDbInfo.COLUMN_STATUS, DownloadConstant.STATUS_PAUSED);
        db.update(DownloadDbInfo.TABLE_NAME, values, DownloadDbInfo.COLUMN_STATUS + " in (" + DownloadConstant.STATUS_READY + "," + DownloadConstant.STATUS_START + "," + DownloadConstant.STATUS_DOWNLOADING + ")", null);
    }
}
