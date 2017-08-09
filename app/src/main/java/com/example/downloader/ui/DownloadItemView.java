package com.example.downloader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.download.task.DownloadTaskInfo;
import com.download.view.DownloadBaseView;
import com.example.downloader.bean.Game;
import com.example.downloader.R;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by yangtianfei on 17-6-27.
 * 自定义list item view
 */
public class DownloadItemView extends DownloadBaseView {
    private ImageView ivIcon;
    private TextView tvTitle;
    private TextView tvDesc;
    private DownloadProgressBar progressBar;
    private Button btStatus;

    private Game game;

    public DownloadItemView(Context context) {
        super(context);
    }

    public DownloadItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DownloadItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setDownloadInfo(DownloadTaskInfo info) {
        super.setDownloadInfo(info);
        game = new Gson().fromJson(info.getExtra(), Game.class);
        ImageLoader.getInstance().displayImage(game.getIcon(), ivIcon);
        tvTitle.setText(game.getName());
        tvDesc.setText(game.getDesc());
    }

    @Override
    protected void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.download_manager_list_item, this);

        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        progressBar = (DownloadProgressBar) findViewById(R.id.list_item3_download_progress);
        btStatus = (Button) findViewById(R.id.bt_status);

        btStatus.setOnClickListener(this);
    }

    @Override
    protected void updateView(DownloadTaskInfo downloadInfo) {
        Log.i("YYDownloadButton", "update view status:" + status);
        int progress = 0;
        if (downloadInfo.getSize() > 0)
            progress = (int) (downloadInfo.getDownloadSize() * 100 / downloadInfo.getSize());
        progressBar.setProgress(progress);
        switch (status) {
            case STATUS_DEFAULT:
                btStatus.setText("开始");
                break;
            case STATUS_READY:
                btStatus.setText("马上开始下载");
                break;
            case STATUS_PAUSE:
                btStatus.setText("开始");
                break;
            case STATUS_START:
                btStatus.setText("暂停");
                break;
            case STATUS_DOWNLOADING:
                btStatus.setText("暂停");
                break;
            case STATUS_CANCEL:
                btStatus.setText("开始");
                break;
            case STATUS_COMPLETE:
                btStatus.setText("已完成");
                break;
            case STATUS_ERROR:
                btStatus.setText("重新开始");
                break;
            default:
                break;
        }
    }
}
