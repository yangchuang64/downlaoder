/*
 * Copyright (C) loongmobile.com, Inc. All Rights Reserved.
 * website: www.loongmobile.com
 */

package com.download.task;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.download.constants.DownloadConstant;
import com.download.db.DownloadDbInfo;
import com.download.db.YYDownloadHelper;
import com.download.util.DownloadFileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends Observable implements Runnable {
    private final String TAG = DownloadTask.class.getSimpleName();

    public static final String TASK_ACTION_COMPLETE = "download_task_action_complete";
    /**
     * Max size of download buffer.
     */
    private static final int MAX_BUFFER_SIZE = 1024 * 32;

    /**
     * Default connect time out.
     */
    private static final int DEFAULT_CONNECT_TIME_OUT = 1000 * 30;

    /**
     * Default read time out.
     */
    private static final int DEFAULT_READ_TIME_OUT = 1000 * 30;

    /**
     * The sleep time of this thread.
     */
    private static final int THREAD_SLEEP_TIME = 500;
    /**
     * Context.
     */
    private Context mContext;

    /**
     * Download target size in bytes.
     */
    private long mSize = -1;
    /**
     * Downloaded size in bytes.
     */
    private volatile long mDownloaded;
    private DownloadTaskInfo downloadInfo;
    /**
     * Current mStatus of download.
     */
    public volatile int mStatus = DownloadConstant.STATUS_READY;

    public int mErrorCode = -1;
    public String errorMsg;
    /**
     * Task launch time.
     */
    public long mLaunchTime = 0;

    public long mStartTime = 0;

    private volatile boolean isRunning;

    /**
     * 默认线程池
     */
    private static final ThreadPoolExecutor singleThreadExecutor =
            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    /**
     * 构造函数
     *
     * @param context
     * @param info
     * @param observer
     * @throws NullPointerException
     */
    public DownloadTask(Context context, DownloadTaskInfo info, Observer observer) throws NullPointerException {
        if (info == null) {
            error(DownloadConstant.ERROR_CODE_INIT_FAIL, DownloadConstant.ERROR_MSG_INIT_FAIL);
            throw new NullPointerException();
        }

        DownloadTaskInfo localInfo = YYDownloadHelper.getInfoByAppDownloadId(context, info.getDownloadId());
        if (localInfo != null) {
            info = localInfo;
        } else {
            long id = YYDownloadHelper.insertApp(context, info.getDownloadId(), info.getType(), info.getTitle(), info.getUrl(), info.getFilePath(), info.getExtra());
            info.setId(id);
        }

        init(context, info, observer);
    }

    void init(Context context, DownloadTaskInfo downloadInfo, Observer observer) {
        mContext = context;
//        mDownloadId = downloadInfo.downloadId;
//        url = downloadInfo.url;
//        mType = downloadInfo.type;
        mSize = downloadInfo.getSize();
        mDownloaded = downloadInfo.getDownloadSize();
//        filePath = downloadInfo.filePath;
        mStatus = downloadInfo.getStatus();
        this.downloadInfo = downloadInfo;
        addObserver(observer);
    }

    @Override
    public void run() {
        if (mStatus == DownloadConstant.STATUS_PAUSED)
            return;

        isRunning = true;

        mStatus = DownloadConstant.STATUS_START;
        stateChanged();
        YYDownloadHelper.updateInfoStatus(mContext, downloadInfo.getDownloadId(), DownloadConstant.STATUS_START);

        if (mLaunchTime == 0)
            mLaunchTime = System.currentTimeMillis();
//        Log.i(TAG, "launch time:" + mLaunchTime);

        try {
            // 下载重试
            boolean retry;
            do {
                try {
                    download();
                    retry = false;
                } catch (DownloadReadException e) {
                    e.printStackTrace();
                    retry = true;
                }
            } while (retry);
        } catch (Exception e) {
            e.printStackTrace();
            error(DownloadConstant.ERROR_CODE_UNKOWN, DownloadConstant.ERROR_MSG_UNKOWN + e.toString());
        } finally {
            isRunning = false;
            getExecutorByType().remove(this);
        }
    }

    private void download() throws DownloadReadException {
        if (mSize > -1 && mSize <= mDownloaded) {
            finishDownLoad();
        }

        RandomAccessFile file = null;
        InputStream stream = null;
        try {
            String urlStr = downloadInfo.getUrl();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(urlStr).addHeader("RANGE", "bytes=" + mDownloaded + "-").build();
            Response response;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                error(DownloadConstant.ERROR_CODE_IO, DownloadConstant.ERROR_MSG_IO + e.toString());
                return;
            }

            // Check for valid content length.
            long contentLength = response.body().contentLength();

            if (contentLength < 1) {
                error(DownloadConstant.ERROR_CODE_EMPTY_TARGET, DownloadConstant.ERROR_MSG_EMPTY_TARGET);
            } else {
                    /*
                     * Set the mSize for this download if it hasn't been already set.
                     */
                if (mSize <= 0) {
                    mSize = contentLength;

                    // update the target size in database
                    ContentValues values = new ContentValues();
                    values.put(DownloadDbInfo.COLUMN_SIZE, mSize);
                    YYDownloadHelper.updateInfoByUrl(mContext, downloadInfo.getDownloadId(), values);
                }
                // Open file and seek to the end of it.
                if (mStartTime == 0)
                    mStartTime = System.currentTimeMillis();
//                Log.i(TAG, "start time:" + mLaunchTime);
//                if (!new File(FileUtils.getDirPath()).exists()) {
//                    error(DownloadConstant.ERROR_CODE_CREATE_DIR, DownloadConstant.ERROR_MSG_CREATE_DIR);
//                    return;
//                }
                // file = new BufferedRandomAccessFile(mDirPath + File.separator + mFileName, "rw");
                try {
                    file = new RandomAccessFile(downloadInfo.getFilePath(), "rw");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    error(DownloadConstant.ERROR_CODE_FILE_NOT_FOUND, DownloadConstant.ERROR_MSG_FILE_NOT_FOUND + e.toString());
                    return;
                }
                try {
                    file.seek(mDownloaded);
                } catch (IOException e) {
                    e.printStackTrace();
                    error(DownloadConstant.ERROR_CODE_FILE_SEEK, DownloadConstant.ERROR_MSG_FILE_SEEK + e.toString());
                    return;
                }

                stream = response.body().byteStream();

                if (mStatus == DownloadConstant.STATUS_START) {
                    mStatus = DownloadConstant.STATUS_DOWNLOADING;
                    YYDownloadHelper.updateInfoStatus(mContext, downloadInfo.getDownloadId(), DownloadConstant.STATUS_DOWNLOADING);
                }
                while (mStatus == DownloadConstant.STATUS_DOWNLOADING) {
                        /*
                         * Size buffer according to how much of the file
                         * is left to download.
                         */
                    byte buffer[];
                    long lastLen = mSize - mDownloaded;
                    if (lastLen > MAX_BUFFER_SIZE) {
                        buffer = new byte[MAX_BUFFER_SIZE];
                    } else if (lastLen <= 0) {
                        buffer = new byte[MAX_BUFFER_SIZE];
                    } else {
                        buffer = new byte[(int) lastLen];
                    }

                    // Read from server into buffer.
                    int read;
                    try {
                        read = stream.read(buffer);
//                        Log.i(TAG + "#2", "read downloadId:" + downloadInfo.getDownloadId() + " buffer size:" + mSize + " download:" + mDownloaded + " read:" + read + " time:" + System.currentTimeMillis());
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new DownloadReadException();
                    }

//                    Log.i(TAG + "#1", "read downloadId:" + downloadInfo.getDownloadId() + " buffer size:" + mSize + " download:" + mDownloaded);
                    if (read == -1) {
                        finishDownLoad();
                        break;
                    }
                    // Write buffer to file.
                    try {
                        file.write(buffer, 0, read);
                    } catch (IOException e) {
                        e.printStackTrace();
                        error(DownloadConstant.ERROR_CODE_FILE_WRITE, DownloadConstant.ERROR_MSG_FILE_WRITE + e.toString());
                        return;
                    }
                    mDownloaded += read;

                    // update the download size in database
                    ContentValues values = new ContentValues();
                    values.put(DownloadDbInfo.COLUMN_DOWNLOAD_SIZE, mDownloaded);
                    YYDownloadHelper.updateInfoByUrl(mContext, downloadInfo.getDownloadId(), values);
                    stateChanged();
                }

                // Close file.
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        error(DownloadConstant.ERROR_CODE_FILE_CLOSE, DownloadConstant.ERROR_MSG_FILE_CLOSE + e.toString());
                    }
                    file = null;
                }
            }
        } finally {
            // Close file.
            if (file != null) {
                try {
                    file.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Close connection to server.
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void finishDownLoad() {
//        Log.i(TAG, "STATUS_COMPLETE");
        mStatus = DownloadConstant.STATUS_COMPLETE;
        stateChanged();
        YYDownloadHelper.updateInfoStatus(mContext, downloadInfo.getDownloadId(), DownloadConstant.STATUS_COMPLETE);

        Intent intent = new Intent();
        intent.setAction(TASK_ACTION_COMPLETE);
        mContext.sendBroadcast(intent, null);
    }

    /**
     * Notify observers that this download's mStatus has changed.
     */
    private void stateChanged() {
        setChanged();
        notifyObservers(mStatus);
    }

    /**
     * Get this task's progress.
     *
     * @return
     */
    public float getProgress() {
        return ((float) mDownloaded / mSize) * 100;
    }

    /**
     * Get this task's ellapsed time.
     *
     * @return
     */
    public String getEllapsedTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar cal = Calendar.getInstance();
        long elapsed = cal.getTimeInMillis();
        elapsed = elapsed - mStartTime;
        cal.setTimeInMillis(elapsed);
        return dateFormat.format(cal.getTime());
    }

    /**
     * Get this task's speed.
     *
     * @return
     */
    public float getSpeed() {
        float s = mDownloaded / 1024;
        Calendar cal = Calendar.getInstance();
        long elapsed = cal.getTimeInMillis();
        elapsed = elapsed - mStartTime;
        elapsed = elapsed / 1000;
        s = s / elapsed;
        return s;
    }

    /**
     * Get this task's remaining time.
     *
     * @return
     */
    public String getRemainingTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar cal = Calendar.getInstance();
        float kbs = (mSize - mDownloaded) / 1024;
        float seconds = kbs / getSpeed();
        long millis = (long) (seconds * 1000);
        cal.setTimeInMillis(millis);
        return dateFormat.format(cal.getTime());
    }

    /**
     * start this task.
     *
     * @return
     */
    public void start() {
//        Log.i(TAG, "start url:" + downloadInfo.getUrl());
        if (mStatus == DownloadConstant.STATUS_ERROR && mErrorCode == DownloadConstant.ERROR_CODE_INIT_FAIL)
            return;

        if (isRunning) {
            stateChanged();
        } else {
            if (mStatus == DownloadConstant.STATUS_COMPLETE) {
                stateChanged();
            } else {
                mStatus = DownloadConstant.STATUS_READY;
                stateChanged();
                getExecutorByType().remove(this);
                getExecutorByType().execute(this);
                YYDownloadHelper.updateInfoStatus(mContext, downloadInfo.getDownloadId(), mStatus);
            }
        }
    }

    /**
     * Pause this task.
     *
     * @return -1 下载初始化错误； 0:暂停下载；
     */
    public int pause() {
//        Log.i(TAG, "pause downloadId:" + downloadInfo.getDownloadId());
        if (mStatus == DownloadConstant.STATUS_ERROR && mErrorCode == DownloadConstant.ERROR_CODE_INIT_FAIL)
            return -1;

        mStatus = DownloadConstant.STATUS_PAUSED;
        stateChanged();
        getExecutorByType().remove(this);
        YYDownloadHelper.updateInfoStatus(mContext, downloadInfo.getDownloadId(), DownloadConstant.STATUS_PAUSED);
        return 0;
    }

    /**
     * Cancel this task.
     */
    public void cancel() {
        mStatus = DownloadConstant.STATUS_CANCELLED;
        stateChanged();

        // delete from database
        YYDownloadHelper.deleteInfo(mContext, downloadInfo.getDownloadId());

        // delete the directory
        DownloadFileUtils.deleteFile(downloadInfo.getFilePath());
    }

    /**
     * get type relative thread pool executor
     *
     * @return
     */
    ThreadPoolExecutor getExecutorByType() {
        return singleThreadExecutor;
    }

    /**
     * Mark this download as having an error.
     *
     * @param errorCode
     * @param msg
     */
    private void error(int errorCode, String msg) {
//        Log.i(TAG, "errorCode:" + errorCode);
        isRunning = false;

        mErrorCode = errorCode;
//        errorMsg = DownloadConstant.errorReason[errorCode];
        errorMsg = msg;

        mStatus = DownloadConstant.STATUS_ERROR;
        // notify user
        stateChanged();
        YYDownloadHelper.updateInfoStatus(mContext, downloadInfo.getDownloadId(), DownloadConstant.STATUS_ERROR);
    }

    public DownloadTaskInfo getDownloadInfo() {
        // 校准状态
        downloadInfo.setDownloadSize(mDownloaded);
        downloadInfo.setSize(mSize);
        downloadInfo.setStatus(mStatus);
        return downloadInfo;
    }

    public long getSize() {
        return mSize;
    }

    public long getDownloadedSize() {
        return mDownloaded;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
