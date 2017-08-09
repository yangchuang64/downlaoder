package com.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.download.constants.DownloadConstant;
import com.download.db.YYDownloadHelper;
import com.download.notification.FileDownloadNotificationHelper;
import com.download.notification.NotificationItem;
import com.download.notification.NotificationTaskListener;
import com.download.task.DownloadTask;
import com.download.task.DownloadTaskInfo;
import com.download.task.DownloadTaskListener;
import com.download.util.DownloadUtils;
import com.download.util.DownloadFileUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by yangtianfei on 17-5-14.
 */
public class DownloaderManager {
    private final String TAG = DownloaderManager.class.getSimpleName();

    private static DownloaderManager mInstance;

    private DownloaderConfiguration configuration;
    /**
     * Download task list.
     */
    private List<DownloadTask> mDownloadTaskList = new ArrayList<DownloadTask>();
    /**
     * waitting download task list, will used when wifi network unavailable
     */
    private List<DownloadTask> mDownloadTaskWaitList = new ArrayList();

    /**
     * Download task observer.
     */
    private DownloadObserver mDownloadObserver = null;

    private NotificationTaskListener notificationTaskListener = new NotificationTaskListener(new FileDownloadNotificationHelper<NotificationItem>());
    /**
     * network connectivity receiver
     */
    private final BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // check wifi is available
            if (DownloadUtils.isWiFiActive(context)) {
                // resume paused task
                Iterator<DownloadTask> iterator = mDownloadTaskWaitList.iterator();
                while (iterator.hasNext()) {
                    DownloadTask task = iterator.next();
                    task.start();
                    iterator.remove();
                    mDownloadTaskList.add(task);
                }
            } else {
                // pause running download task
                Iterator<DownloadTask> iterator = mDownloadTaskList.iterator();
                while (iterator.hasNext()) {
                    DownloadTask task = iterator.next();
                    if (task.mStatus == DownloadConstant.STATUS_DOWNLOADING
                            || task.mStatus == DownloadConstant.STATUS_READY || task.mStatus == DownloadConstant.STATUS_START) {
                        task.pause();
                        iterator.remove();
                        mDownloadTaskWaitList.add(task);
                    }
                }
            }
        }
    };

    public synchronized static final DownloaderManager getInstance() {
        if (mInstance == null) {
            mInstance = new DownloaderManager();
        }
        return mInstance;
    }

    private DownloaderManager() {
        // init local download observer
        mDownloadObserver = new DownloadObserver() {
            @Override
            public void update(Observable observable, Object data) {
                super.update(observable, data);
                DownloadTask task = (DownloadTask) observable;
                int taskStatus = task.mStatus;

                if (taskStatus == DownloadConstant.STATUS_COMPLETE
                        || taskStatus == DownloadConstant.STATUS_CANCELLED) {
                    // remove this task
                    // if it's network bug, just pause the task.
                    mDownloadTaskList.remove(task);
                }
            }
        };
    }

    public synchronized void init(DownloaderConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("download configuration is null");
        }
        this.configuration = configuration;
        if (configuration.mDefaultTaskListener != null)
            addTaskListener(configuration.mDefaultTaskListener);

        // create the application setup dir.
        DownloadFileUtils.createExternalStorageDir(configuration.mDownloadDir);

        // correct the applications' status
        YYDownloadHelper.correctInfoStatus(configuration.mContext);

        // register network connectivity receiver
        IntentFilter netFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        configuration.mContext.registerReceiver(netReceiver, netFilter);
    }

    public DownloaderConfiguration getConfiguration() {
        return configuration;
    }

    public void setIsDownloadOnlyOnWifi(boolean isOnlyOnWifi) {
        configuration.mIsDownloadOnlyOnWifi = isOnlyOnWifi;
    }

    /**
     * Open the download manager to get ready to downloading.
     */
    public final void open() {
        initAllDownloadingTask();
    }

    /**
     * Initialize all downloading tasks.
     */
    private void initAllDownloadingTask() {
        if (mDownloadTaskList.size() == 0) {
            // find paused tasks
            List<DownloadTaskInfo> infos = YYDownloadHelper.getPausedInfo(configuration.mContext);
            for (DownloadTaskInfo info : infos) {
                DownloadTask task = new DownloadTask(configuration.mContext, info, mDownloadObserver);
                task.start();
                mDownloadTaskList.add(task);
            }
        }
    }

    /**
     * Close the download manager.
     */
    public final void close() {
        // pause all downloading tasks
        if (getDownloadingTaskNum() > 0) {
            pauseAllDownloadTask();
        }
    }


    /**
     * add download task
     *
     * @param downloadId
     * @param url
     * @return
     */
    public void addDownloadTask(String downloadId, int type, String title, String url, String extra) {
        DownloadTaskInfo taskInfo = DownloadUtils.createDownloadTaskInfo(downloadId, type, title, url, configuration.mDownloadDir + DownloadFileUtils.getFileName(url), extra);
        addDownloadTask(taskInfo);
    }

    public void addDownloadTask(DownloadTaskInfo taskInfo) {
        if (taskInfo == null || TextUtils.isEmpty(taskInfo.getDownloadId()) || TextUtils.isEmpty(taskInfo.getUrl())) {
            throw new IllegalArgumentException("download info is error");
        }

        DownloadTask task = getAppDownloadTask(taskInfo.getDownloadId());
        if (task == null) {
            // 需要设置下载目录
            taskInfo.setFilePath( configuration.mDownloadDir + DownloadFileUtils.getFileName(taskInfo.getUrl()));
            task = new DownloadTask(configuration.mContext, taskInfo, mDownloadObserver);
            mDownloadTaskList.add(task);
        }
        if (!configuration.mIsDownloadOnlyOnWifi || (configuration.mIsDownloadOnlyOnWifi && DownloadUtils.isWiFiActive(configuration.mContext))) {
            if (!task.isRunning()) {
                task.start();
            }
        }
    }

    /**
     * Pause a downloading task.
     *
     * @param downloadId
     * @return
     */
    public boolean pauseTask(String downloadId) {
        DownloadTask task = getAppDownloadTask(downloadId);
        if (task != null) {
            task.pause();
        }
        return true;
    }

    /**
     * Pause all downloading task.
     */
    public void pauseAllDownloadTask() {
        int size = mDownloadTaskList.size();
        for (int index = 0; index < size; index++) {
            DownloadTask task = mDownloadTaskList.get(index);
            int status = task.mStatus;
            if (status == DownloadConstant.STATUS_DOWNLOADING || status == DownloadConstant.STATUS_READY) {
                task.pause();
            }
        }
    }

    /**
     * Resume a paused task.
     *
     * @param downloadId
     * @return
     */
    public boolean resumeTask(String downloadId) {
        DownloadTask task = getAppDownloadTask(downloadId);
        if (task != null) {
            task.start();
        } else {
            DownloadTaskInfo info = YYDownloadHelper.getInfoByAppDownloadId(configuration.mContext, downloadId);
            if (info == null)
                return false;
            task = new DownloadTask(configuration.mContext, info, mDownloadObserver);
            task.start();
            mDownloadTaskList.add(task);
        }
        return true;
    }

    /**
     * resume all paused task
     */
    public void resumeAllTask() {
        int size = mDownloadTaskList.size();
        for (int index = 0; index < size; index++) {
            DownloadTask task = mDownloadTaskList.get(index);
            int status = task.mStatus;
            if (status == DownloadConstant.STATUS_PAUSED) {
                task.start();
            }
        }
    }

    /**
     * Cancel a downloading task.
     *
     * @return
     */
    public boolean cancelTask(String downloadId) {
        DownloadTask task = getAppDownloadTask(downloadId);
        if (task != null) {
            task.cancel();
            mDownloadTaskList.remove(task);
        } else {
            // something error occurs, just delete this application here
            DownloadTaskInfo info = YYDownloadHelper.getInfoByAppDownloadId(configuration.mContext.getApplicationContext(), downloadId);
            if (info != null) {
                // delete the directory
                DownloadFileUtils.deleteFile(info.getFilePath());
            }

            YYDownloadHelper.deleteInfo(configuration.mContext.getApplicationContext(), downloadId);
        }
        return true;
    }

    /**
     * Remove application by id.
     *
     * @return
     */
    public boolean removeApp(String downloadId) {
        DownloadTaskInfo info = YYDownloadHelper.getInfoByAppDownloadId(configuration.mContext, downloadId);
        if (info != null) {
            // delete the application from database
            YYDownloadHelper.deleteInfo(configuration.mContext, downloadId);

            // delete the application's directory
            DownloadFileUtils.deleteFile(info.getFilePath());
        }
        return false;
    }

    /**
     * get download task by url
     *
     * @param downloadId
     * @return
     */
    private DownloadTask getAppDownloadTask(String downloadId) {
        for (int index = 0; index < mDownloadTaskList.size(); index++) {
            DownloadTask task = mDownloadTaskList.get(index);
            if (task.getDownloadInfo().getDownloadId().equals(downloadId))
                return task;
        }
        return null;
    }

    /**
     * Get the number of downloading task.
     *
     * @return
     */
    public int getDownloadingTaskNum() {
        int totalNum = mDownloadTaskList.size();
        if (totalNum <= 0)
            return 0;

        int downloadingNum = 0;
        for (int index = 0; index < totalNum; index++) {
            DownloadTask task = mDownloadTaskList.get(index);
            if (task != null && task.mStatus == DownloadConstant.STATUS_DOWNLOADING) {
                downloadingNum++;
            }
        }
        return downloadingNum;
    }

    /**
     * get all download task from sqlite
     *
     * @return
     */
    public List<DownloadTaskInfo> getAllDownloadFromCache() {
        return YYDownloadHelper.getAllDownloadInfo(configuration.mContext, null);
    }

    public boolean isDownlaoding(String url) {
        DownloadTask task = getAppDownloadTask(url);
        return task.isRunning();
    }

    /**
     * correct task status cache
     */
    public void correctTaskStatus() {
        // correct the applications' status
        YYDownloadHelper.correctInfoStatus(configuration.mContext);
    }

    /**
     * add task listener
     *
     * @param listener
     */
    public void addTaskListener(DownloadTaskListener listener) {
        mDownloadObserver.addTaskListener(listener);
    }

    /**
     * add task listener
     *
     * @param downloadId
     * @param listener
     */
    public void addTaskListener(String downloadId, DownloadTaskListener listener) {
        mDownloadObserver.addTaskListener(new TaskListenerWrraper(downloadId, listener));
    }

    /**
     * remove task listener
     *
     * @param listener
     */
    public void removeTaskListener(DownloadTaskListener listener) {
        mDownloadObserver.deleteObserver(listener);
    }

    class DownloadObserver implements Observer {
        private final ArrayList<DownloadTaskListener> listeners;
        private Handler uiHandler;

        public DownloadObserver() {
            listeners = new ArrayList<>();
            uiHandler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void update(final Observable observable, final Object data) {
            final DownloadTask task = (DownloadTask) observable;
            final DownloadTaskInfo info = task.getDownloadInfo();
//            Log.i(TAG, "update downloadId:" + info.getDownloadId() + " status:" + task.mStatus);
            switch (task.mStatus) {
                case DownloadConstant.STATUS_READY:
                    onReady(info);
                    break;
                case DownloadConstant.STATUS_START:
                    onStart(info);
                    break;
                case DownloadConstant.STATUS_PAUSED:
                    onPause(info);
                    break;
                case DownloadConstant.STATUS_DOWNLOADING:
                    update(info, task.getSize(), task.getDownloadedSize());
                    break;
                case DownloadConstant.STATUS_CANCELLED:
                    onCancel(info);
                    break;
                case DownloadConstant.STATUS_COMPLETE:
                    onComplete(info, info.getFilePath());
                    break;
                case DownloadConstant.STATUS_ERROR:
                    onFail(info, task.mErrorCode, task.errorMsg);
                    break;
                default:
                    break;
            }
        }

        void onReady(final DownloadTaskInfo downloadInfo) {
            if (configuration.mIsNotifition)
                notificationTaskListener.onReady(downloadInfo);
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (DownloadTaskListener listener : listeners)
                        listener.onReady(downloadInfo);
                }
            });
        }

        void onStart(final DownloadTaskInfo downloadInfo) {
            if (configuration.mIsNotifition)
                notificationTaskListener.onStart(downloadInfo);
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (DownloadTaskListener listener : listeners)
                        listener.onStart(downloadInfo);
                }
            });
        }

        void onPause(final DownloadTaskInfo downloadInfo) {
            if (configuration.mIsNotifition)
                notificationTaskListener.onPause(downloadInfo);
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (DownloadTaskListener listener : listeners)
                        listener.onPause(downloadInfo);
                }
            });
        }

        void update(final DownloadTaskInfo downloadInfo, final long tatolSize, final long downloadSize) {
            if (configuration.mIsNotifition)
                notificationTaskListener.update(downloadInfo, tatolSize, downloadSize);
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (DownloadTaskListener listener : listeners)
                        listener.update(downloadInfo, tatolSize, downloadSize);
                }
            });
        }

        void onCancel(final DownloadTaskInfo downloadInfo) {
            if (configuration.mIsNotifition)
                notificationTaskListener.onCancel(downloadInfo);
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (DownloadTaskListener listener : listeners)
                        listener.onCancel(downloadInfo);
                }
            });
        }

        void onComplete(final DownloadTaskInfo downloadInfo, final String path) {
            if (configuration.mIsNotifition)
                notificationTaskListener.onComplete(downloadInfo, downloadInfo.getFilePath());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (DownloadTaskListener listener : listeners)
                        listener.onComplete(downloadInfo, path);
                }
            });
        }

        void onFail(final DownloadTaskInfo downloadInfo, final int code, final String msg) {
            if (configuration.mIsNotifition)
                notificationTaskListener.onFail(downloadInfo, code, msg);
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (DownloadTaskListener listener : listeners)
                        listener.onFail(downloadInfo, code, msg);
                }
            });
        }

        public <T extends DownloadTaskListener> void addTaskListener(T listener) {
            if (listener == null)
                throw new NullPointerException();
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }

        public synchronized void deleteObserver(DownloadTaskListener listener) {
            if (listener == null)
                return;
            Iterator<DownloadTaskListener> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                DownloadTaskListener listener1 = iterator.next();
                if (listener1.equals(listener)) {
                    iterator.remove();
                }
            }
        }
    }

    class TaskListenerWrraper implements DownloadTaskListener {

        private String downloadId;
        private DownloadTaskListener mListener;

        public TaskListenerWrraper(String downloadId, DownloadTaskListener listener) {
            this.downloadId = downloadId;
            mListener = listener;
        }

        @Override
        public void onReady(DownloadTaskInfo downloadInfo) {
            if (downloadId.equals(downloadInfo.getDownloadId()))
                mListener.onReady(downloadInfo);
        }

        @Override
        public void onStart(DownloadTaskInfo downloadInfo) {
            if (downloadId.equals(downloadInfo.getDownloadId()))
                mListener.onStart(downloadInfo);
        }

        @Override
        public void onPause(DownloadTaskInfo downloadInfo) {
            if (downloadId.equals(downloadInfo.getDownloadId()))
                mListener.onPause(downloadInfo);
        }

        @Override
        public void update(DownloadTaskInfo downloadInfo, long tatolSize, long downloadSize) {
//            Log.i(TAG, "TaskListenerWrraper downloadId:" + downloadInfo.getDownloadId());
            if (downloadId.equals(downloadInfo.getDownloadId())) {
//                Log.i(TAG, "TaskListenerWrraper 2");
                mListener.update(downloadInfo, tatolSize, downloadSize);
            }
        }

        @Override
        public void onCancel(DownloadTaskInfo downloadInfo) {
            if (downloadId.equals(downloadInfo.getDownloadId()))
                mListener.onCancel(downloadInfo);
        }

        @Override
        public void onComplete(DownloadTaskInfo downloadInfo, String path) {
            if (downloadId.equals(downloadInfo.getDownloadId()))
                mListener.onComplete(downloadInfo, path);
        }

        @Override
        public void onFail(DownloadTaskInfo downloadInfo, int code, String msg) {
            if (downloadId.equals(downloadInfo.getDownloadId()))
                mListener.onFail(downloadInfo, code, msg);
        }

        @Override
        public boolean equals(Object obj) {
            boolean equals = super.equals(obj);
            if (!equals) {
                equals = mListener.equals(obj);
            }
            return equals;
        }
    }
}
