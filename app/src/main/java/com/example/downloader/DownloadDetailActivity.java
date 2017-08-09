package com.example.downloader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.download.constants.DownloadConstant;
import com.download.view.DownloadButton;
import com.example.downloader.bean.Game;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by yangtianfei on 17-6-27.
 * 下载详情页
 */
public class DownloadDetailActivity extends Activity {
    private final String TAG = DownloadDetailActivity.class.getSimpleName();

    private ImageView ivIcon;
    private TextView tvName;
    private TextView tvDesc;
    private DownloadButton downloadButton;

    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        tvName = (TextView) findViewById(R.id.name);
        tvDesc = (TextView) findViewById(R.id.desc);
        downloadButton = (DownloadButton) findViewById(R.id.bt);

        String gameStr = getIntent().getStringExtra("extra");
        if (gameStr == null)
            return;

        game = new Gson().fromJson(gameStr, Game.class);
        ImageLoader.getInstance().displayImage(game.getIcon(), ivIcon);
        tvName.setText(game.getName());
        tvDesc.setText(game.getDesc());

        downloadButton.setDownloadInfo(game.getGid(), game.getName(), 0, game.getUrl(), gameStr);
    }
}
