/*
 * Copyright (C) loongmobile.com, Inc. All Rights Reserved.
 * website: www.loongmobile.com
 */

package com.download.db;

import android.provider.BaseColumns;

/**
 * This class define Application information columns in database.
 */
public final class DownloadDbInfo implements BaseColumns {

    public static final String DEFAULT_SORT_ORDER = "created DESC";

    public static final String DATABASE_NAME = "download.db";

    /**
     * table name.
     */
    public static final String TABLE_NAME = "download_table";

    public static final int DATABASE_VERSION = 2;

    /**
     * download uri.
     */
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "name";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_IS_MOD = "is_mod";
    /**
     * download size.
     */
    public static final String COLUMN_SIZE = "size";

    /**
     * downloaded size.
     */
    public static final String COLUMN_DOWNLOAD_SIZE = "download_size";

    /**
     * download position.
     */
    public static final String COLUMN_DOWNLOAD_POSITION = "download_postion";
    public static final String COLUMN_EXTRA = "extra";
    /**
     * status: downloading.
     */
    public static final String COLUMN_STATUS = "status";

    public static final String COLUMN_CREATE_TIME = "create_time";
    public static final String COLUMN_START_TIME = "start_time";
}
