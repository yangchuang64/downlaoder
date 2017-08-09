package com.example.downloader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.download.DownloaderManager;
import com.download.task.DownloadTaskInfo;
import com.download.task.DownloadTaskListener;
import com.example.downloader.R;

/**
 * This class is the downloading progress bar.
 */
public class DownloadProgressBar extends LinearLayout implements DownloadTaskListener {
    private final String TAG = DownloadProgressBar.class.getSimpleName();
    Context mContext;

    TextView mTextView;

    ProgressBar mProgressBar;

    private String mDownloadId;

    public DownloadProgressBar(Context context) {
        super(context);
        initialize(context);
    }

    public DownloadProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        mContext = context;
        LayoutInflater factory = LayoutInflater.from(context);
        factory.inflate(R.layout.download_progress_bar, this);

        mTextView = (TextView) findViewById(R.id.download_progress_bar_txt);
        mProgressBar = (ProgressBar) findViewById(R.id.download_progress_bar_progress);
    }

    /**
     * Set the current progress.
     *
     * @param progress
     */
    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    /**
     * Set the current text.
     *
     * @param resid
     */
    public void setText(int resid) {
        mTextView.setText(resid);
    }

    /**
     * Set the current text color
     *
     * @param color
     */
    private void setTextColor(int color) {
        mTextView.setTextColor(color);
    }

    /**
     * Set the current text background color
     *
     * @param color
     */
    private void setTextBackgroundColor(int color) {
        mTextView.setBackgroundColor(color);
    }

    /**
     * Show text view.
     */
    public void showText() {
        mTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Hide text view.
     */
    public void hideText() {
        mTextView.setVisibility(View.GONE);
    }

    public void setData(String downloadId, long size, long downloadSize) {
        Log.i(TAG, "update mUrl:" + mDownloadId + " downloadId:" + downloadId);
        if (mDownloadId != null && mDownloadId.equals(downloadId))
            return;
        mDownloadId = downloadId;
        int progress = (int) (downloadSize * 100 / size);
        setProgress(progress);
        DownloaderManager.getInstance().removeTaskListener(this);
        DownloaderManager.getInstance().addTaskListener(mDownloadId, this);
    }

    @Override
    public void onReady(DownloadTaskInfo downloadInfo) {

    }

    @Override
    public void onStart(DownloadTaskInfo downloadInfo) {

    }

    @Override
    public void onPause(DownloadTaskInfo downloadInfo) {

    }

    @Override
    public void update(DownloadTaskInfo downloadInfo, long tatolSize, long downloadSize) {
        int progress = (int) (downloadSize * 100 / tatolSize);
        Log.i(TAG, "update mDownloadId:" + mDownloadId + " downloadId:" + downloadInfo.getDownloadId() + " mDownloaded:" + downloadSize + " size:" + tatolSize + " progress:" + progress);
        setProgress(progress);
    }

    @Override
    public void onCancel(DownloadTaskInfo downloadInfo) {

    }

    @Override
    public void onComplete(DownloadTaskInfo downloadInfo, String s1) {

    }

    @Override
    public void onFail(DownloadTaskInfo downloadInfo, int i, String s1) {

    }
}
