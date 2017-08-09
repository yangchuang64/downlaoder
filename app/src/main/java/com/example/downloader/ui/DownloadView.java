package com.example.downloader.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.download.DownloaderManager;
import com.download.task.DownloadTaskInfo;
import com.download.task.DownloadTaskListener;
import com.example.downloader.R;

/**
 * Created by yangtianfei on 17-5-20.
 */

public class DownloadView  extends LinearLayout implements DownloadTaskListener {

    private final String TAG = DownloadView.class.getSimpleName();

    TextView tvTitle;
    DownloadProgressBar progressBar;
    Button btCancel, btStatus;

    private DownloadTaskInfo mInfo;
    public DownloadView(Context context) {
        super(context);
    }

    public DownloadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public DownloadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.download_manager_list_item, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        progressBar = (DownloadProgressBar) findViewById(R.id.list_item3_download_progress);
        btCancel = (Button) findViewById(R.id.bt_cancel);
        btStatus = (Button) findViewById(R.id.bt_status);

    }

    public void setData(DownloadTaskInfo info) {
        if (mInfo != null && mInfo.getDownloadId() == info.getDownloadId())
            return;

        mInfo = info;
        int progress = (int) (info.getDownloadSize() * 100 / info.getSize());
        progressBar.setProgress(progress);
        DownloaderManager.getInstance().removeTaskListener(this);
        DownloaderManager.getInstance().addTaskListener(info.getDownloadId(), this);
    }

    @Override
    public void onReady(DownloadTaskInfo downloadInfo) {
        btStatus.setText("等待");
    }

    @Override
    public void onStart(DownloadTaskInfo downloadInfo) {
        btStatus.setText("下载中");
    }

    @Override
    public void onPause(DownloadTaskInfo downloadInfo) {
        btStatus.setText("暂停");
    }

    @Override
    public void update(DownloadTaskInfo downloadInfo, long tatolSize, long downloadSize) {
        btStatus.setText("下载中");
        int progress = (int) (downloadSize * 100 / tatolSize);
        progressBar.setProgress(progress);
    }

    @Override
    public void onCancel(DownloadTaskInfo downloadInfo) {
        btStatus.setText("取消");
    }

    @Override
    public void onComplete(DownloadTaskInfo downloadInfo, String path) {
        btStatus.setText("完成");
    }

    @Override
    public void onFail(DownloadTaskInfo downloadInfo, int code, String msg) {
        btStatus.setText("出错");
    }
}
