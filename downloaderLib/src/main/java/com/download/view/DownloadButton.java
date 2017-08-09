package com.download.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.download.R;
import com.download.task.DownloadTaskInfo;

/**
 * Created by yangtianfei on 17-6-27.
 *
 *
 */
public class DownloadButton extends DownloadBaseView {

    private String text;
    private String completeText;

    private TextView btDownload;
    private ProgressBar progressBar;

    private OnClickListener mOnClickListener;
    private OnDownloadCompleteClickListener onDownloadCompleteClickListener;

    public DownloadButton(Context context) {
        super(context);
    }

    public DownloadButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.download);
        text = a.getString(R.styleable.download_text);
        completeText = a.getString(R.styleable.download_textComplete);
        int textSize = a.getDimensionPixelSize(R.styleable.download_textSize, 0);
        Drawable progressDrawable = a.getDrawable(R.styleable.download_progressDrawable);

        if (text == null) {
            text = getContext().getString(R.string.download_start);
        }
        btDownload.setText(text);

        if (textSize > 0)
            btDownload.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        if (progressDrawable != null)
            progressBar.setProgressDrawable(progressDrawable);
    }

    @Override
    protected void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.download_button, this);

        btDownload = (TextView) findViewById(R.id.bt_download);
        progressBar = (ProgressBar) findViewById(R.id.download_progress_bar_progress);
        btDownload.setOnClickListener(this);
    }

    @Override
    protected void updateView(DownloadTaskInfo downloadInfo) {
//        Log.i("YYDownloadButton", "update view status:" + status);
        int progress = 0;
        if (downloadInfo.getSize() > 0)
            progress = (int) (downloadInfo.getDownloadSize() * 100 / downloadInfo.getSize());
        progressBar.setProgress(progress);
        switch (status) {
            case STATUS_DEFAULT:
                btDownload.setText(text);
                btDownload.setTextColor(getContext().getResources().getColor(android.R.color.white));
                progressBar.setProgress(100);
                break;
            case STATUS_READY:
                btDownload.setText(R.string.download_pause);
                btDownload.setTextColor(getContext().getResources().getColor(android.R.color.black));
                break;
            case STATUS_PAUSE:
                btDownload.setText(R.string.download_resume);
                btDownload.setTextColor(getContext().getResources().getColor(android.R.color.black));
                break;
            case STATUS_START:
                btDownload.setText(R.string.download_pause);
                btDownload.setTextColor(getContext().getResources().getColor(android.R.color.black));
                break;
            case STATUS_DOWNLOADING:
                btDownload.setText(R.string.download_pause);
                btDownload.setTextColor(getContext().getResources().getColor(android.R.color.black));
                break;
            case STATUS_CANCEL:
                btDownload.setText(text);
                btDownload.setTextColor(getContext().getResources().getColor(android.R.color.black));
                break;
            case STATUS_COMPLETE:
                btDownload.setText(TextUtils.isEmpty(completeText) ? getContext().getString(R.string.download_complete) : completeText);
                btDownload.setTextColor(getContext().getResources().getColor(android.R.color.white));
                break;
            case STATUS_ERROR:
                btDownload.setText(text);
                btDownload.setTextColor(getContext().getResources().getColor(android.R.color.black));
                break;
            default:
                break;
        }
    }

    public void setDownloadInfo(String downloadId, String title, int type, String url, String extra, OnDownloadCompleteClickListener listener) {
        setDownloadInfo(downloadId, title, type, url, extra);
        onDownloadCompleteClickListener = listener;
    }

    public void setText(String text) {
        btDownload.setText(text);
        btDownload.setTextColor(getContext().getResources().getColor(android.R.color.white));
        progressBar.setProgress(100);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (mOnClickListener != null)
            mOnClickListener.onClick(view);
    }

    @Override
    protected void onCompleteClick() {
        super.onCompleteClick();
        if (onDownloadCompleteClickListener != null) {
            onDownloadCompleteClickListener.onCompleteClick();
        }
    }

    public void setOnDownloadCompleteClickListener(OnDownloadCompleteClickListener onDownloadCompleteClickListener) {
        this.onDownloadCompleteClickListener = onDownloadCompleteClickListener;
    }

    public interface OnDownloadCompleteClickListener {
        void onCompleteClick();
    }
}
