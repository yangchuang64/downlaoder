package com.download.view;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.download.DownloaderManager;
import com.download.constants.DownloadConstant;
import com.download.db.YYDownloadHelper;
import com.download.task.DownloadTaskInfo;
import com.download.task.DownloadTaskListener;

import java.io.File;

/**
 * 下载基类
 *
 * @author yangtianfei
 */
public abstract class DownloadBaseView extends FrameLayout implements View.OnClickListener, DownloadTaskListener {
    private final String TAG = DownloadBaseView.class.getSimpleName();

    protected final int STATUS_DEFAULT = 0x10;
    protected final int STATUS_READY = 0x11;
    protected final int STATUS_START = 0x12;
    protected final int STATUS_PAUSE = 0x13;
    protected final int STATUS_DOWNLOADING = 0x14;
    protected final int STATUS_CANCEL = 0x15;
    protected final int STATUS_COMPLETE = 0x16;
    protected final int STATUS_ERROR = 0x17;

//    protected String downloadId;
//    protected String title;
//    protected int type;
//    protected String url;
//    protected String extra;
    protected DownloadTaskInfo downloadTaskInfo;

    protected int status = STATUS_DEFAULT;
    private TaskListenerWrapper taskListener;

    public DownloadBaseView(Context context) {
        super(context);
        init();
    }

    public DownloadBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DownloadBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
        taskListener = new TaskListenerWrapper(this);
    }

    protected abstract void initView();

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloaderManager.getInstance().removeTaskListener(this);
    }

    /**
     * 初始化下载数据
     */
    public void setDownloadInfo(String downloadId, String title, int type, String url, String extra) {
//        Log.i(TAG, "id:" + downloadId);

        downloadTaskInfo = new DownloadTaskInfo();
        downloadTaskInfo.setDownloadId(downloadId);
        downloadTaskInfo.setType(type);
        downloadTaskInfo.setTitle(title);
        downloadTaskInfo.setUrl(url);
        downloadTaskInfo.setExtra(extra);

        setDownloadInfo(downloadTaskInfo);
    }

    /**
     * 初始化下载数据
     *
     * @param info
     */
    public void setDownloadInfo(DownloadTaskInfo info) {
//        setDownloadInfo(info.getDownloadId(), info.getName(), info.getType(), info.getUrl(), info.getExtra());
        this.downloadTaskInfo = info;
        update(info.getDownloadId());

        DownloaderManager.getInstance().removeTaskListener(taskListener);
        DownloaderManager.getInstance().addTaskListener(info.getDownloadId(), taskListener);
    }

    /**
     * 更新状态
     *
     * @param downloadId
     */
    protected void update(String downloadId) {
        DownloadTaskInfo localDownloadTaskInfo = YYDownloadHelper.getInfoByAppDownloadId(getContext(), downloadId);
        if (localDownloadTaskInfo != null)
            downloadTaskInfo = localDownloadTaskInfo;

        switch (downloadTaskInfo.getStatus()) {
            case DownloadConstant.STATUS_READY:
                status = STATUS_READY;
                break;
            case DownloadConstant.STATUS_PAUSED:
                status = STATUS_PAUSE;
                break;
            case DownloadConstant.STATUS_START:
                status = STATUS_START;
                break;
            case DownloadConstant.STATUS_DOWNLOADING:
                status = STATUS_DOWNLOADING;
                break;
            case DownloadConstant.STATUS_CANCELLED:
                status = STATUS_CANCEL;
                break;
            case DownloadConstant.STATUS_COMPLETE:
                status = STATUS_COMPLETE;
                break;
            case DownloadConstant.STATUS_ERROR:
                status = STATUS_ERROR;
                break;
            default:
                status = STATUS_DEFAULT;
                break;
        }
//        YYDownloadInfo.filePath = info.filePath;
//        YYDownloadInfo.setFinishedSize(info.downloadSize);
//        YYDownloadInfo.setTotalSize(info.size);
        updateView(downloadTaskInfo);
    }

    protected abstract void updateView(DownloadTaskInfo downloadTaskInfo);

    @Override
    public void onClick(View view) {
//        if (MachineUtils.isNativeProcess()) {
//            NewGGToast.showBottomToast(getContext().getString(R.string.sandbox_str_unsupport_mod_toast));
//            return;
//        }
        if (downloadTaskInfo == null || TextUtils.isEmpty(downloadTaskInfo.getUrl()))
            return;
//        Log.i(TAG, "status:" + status);
        switch (status) {
            case STATUS_DEFAULT:
                startDownload();
                break;
            case STATUS_READY:
                pauseDownload();
                break;
            case STATUS_PAUSE:
                resumeDownload();
                break;
            case STATUS_START:
            case STATUS_DOWNLOADING:
                pauseDownload();
                break;
            case STATUS_CANCEL:
                break;
            case STATUS_COMPLETE:
                onCompleteClick();
                break;
            case STATUS_ERROR:
                resumeDownload();
                break;
        }
    }

    /**
     * 开始下载
     */
    private void startDownload() {
//        YYDownloadManager.getInstance().addDownloadTask(
//                downloadTaskInfo.getDownloadId(), downloadTaskInfo.getType(), downloadTaskInfo.getTitle(), downloadTaskInfo.getUrl(), downloadTaskInfo.getExtra());
        DownloaderManager.getInstance().addDownloadTask(downloadTaskInfo);
    }

    /**
     * 重新下载
     */
    private void resumeDownload() {
        DownloaderManager.getInstance().resumeTask(downloadTaskInfo.getDownloadId());
    }

    /**
     * 暂停下载
     */
    private void pauseDownload() {
        DownloaderManager.getInstance().pauseTask(downloadTaskInfo.getDownloadId());
    }

    protected void onCompleteClick() {

    }

    /**
     * 是否超出手机可用空间
     *
     * @return
     */
    private boolean isUnAvailable(long size) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks < size;
    }

    @Override
    public void onReady(DownloadTaskInfo downloadTaskInfo) {

    }

    @Override
    public void onStart(DownloadTaskInfo downloadTaskInfo) {

    }

    @Override
    public void onPause(DownloadTaskInfo downloadTaskInfo) {

    }

    @Override
    public void update(DownloadTaskInfo downloadTaskInfo, long tatolSize, long downloadSize) {

    }

    @Override
    public void onCancel(DownloadTaskInfo downloadTaskInfo) {

    }

    @Override
    public void onComplete(DownloadTaskInfo downloadTaskInfo, String path) {

    }

    @Override
    public void onFail(DownloadTaskInfo downloadTaskInfo, int code, String msg) {

    }

    class TaskListenerWrapper implements DownloadTaskListener {

        private DownloadTaskListener taskListener;

        public TaskListenerWrapper(DownloadTaskListener taskListener) {
            this.taskListener = taskListener;
        }

        @Override
        public void onReady(DownloadTaskInfo downloadTaskInfo) {
            status = STATUS_READY;
            updateView(downloadTaskInfo);
            if (taskListener != null) {
                taskListener.onReady(downloadTaskInfo);
            }
        }

        @Override
        public void onStart(DownloadTaskInfo downloadTaskInfo) {
            status = STATUS_START;
            updateView(downloadTaskInfo);
            if (taskListener != null) {
                taskListener.onStart(downloadTaskInfo);
            }
        }

        @Override
        public void onPause(DownloadTaskInfo downloadTaskInfo) {
            status = STATUS_PAUSE;
            updateView(downloadTaskInfo);
            if (taskListener != null) {
                taskListener.onPause(downloadTaskInfo);
            }
        }

        @Override
        public void update(DownloadTaskInfo downloadTaskInfo, long tatolSize, long downloadSize) {
            status = STATUS_DOWNLOADING;
            downloadTaskInfo.setDownloadSize(downloadSize);
            downloadTaskInfo.setSize(tatolSize);
            updateView(downloadTaskInfo);
            if (taskListener != null) {
                taskListener.update(downloadTaskInfo, tatolSize, downloadSize);
            }
        }

        @Override
        public void onCancel(DownloadTaskInfo downloadTaskInfo) {
            status = STATUS_CANCEL;
            updateView(downloadTaskInfo);
            if (taskListener != null) {
                taskListener.onCancel(downloadTaskInfo);
            }
        }

        @Override
        public void onComplete(DownloadTaskInfo downloadTaskInfo, String path) {
            status = STATUS_COMPLETE;
            updateView(downloadTaskInfo);
            downloadTaskInfo.setFilePath(path);
            if (taskListener != null) {
                taskListener.onComplete(downloadTaskInfo, path);
            }
        }

        @Override
        public void onFail(DownloadTaskInfo downloadTaskInfo, int code, String msg) {
            status = STATUS_ERROR;
            updateView(downloadTaskInfo);
            if (taskListener != null) {
                taskListener.onFail(downloadTaskInfo, code, msg);
            }
        }
    }
}
