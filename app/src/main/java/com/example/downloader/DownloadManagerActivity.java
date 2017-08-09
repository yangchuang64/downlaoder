package com.example.downloader;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.download.DownloaderManager;
import com.download.constants.DownloadConstant;
import com.download.task.DownloadTaskInfo;
import com.download.task.DownloadTaskListener;
import com.example.downloader.bean.Game;
import com.example.downloader.ui.DownloadProgressBar;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 下载管理页
 */
public class DownloadManagerActivity extends AppCompatActivity implements DownloadTaskListener {
    private final String TAG = DownloadManagerActivity.class.getSimpleName();

    ListView lv;
    ManagerListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        lv = (ListView) findViewById(R.id.list);
        listAdapter = new ManagerListAdapter();
        lv.setAdapter(listAdapter);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                List<DownloadTaskInfo> infos = DownloaderManager.getInstance().getAllDownloadFromCache();
                listAdapter.setItems(infos);
            }
        });
        // 添加下载监听
        DownloaderManager.getInstance().addTaskListener(DownloadManagerActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消下载监听
        DownloaderManager.getInstance().removeTaskListener(DownloadManagerActivity.this);
    }

    /**
     * 下载开始
     *
     * @param downloadInfo
     */
    @Override
    public void onReady(DownloadTaskInfo downloadInfo) {
        listAdapter.update(downloadInfo.getDownloadId(), DownloadConstant.STATUS_READY);
    }

    /**
     * 下载开始
     *
     * @param downloadInfo
     */
    @Override
    public void onStart(DownloadTaskInfo downloadInfo) {
        listAdapter.update(downloadInfo.getDownloadId(), DownloadConstant.STATUS_START);
    }

    /**
     * 下载暂停
     *
     * @param downloadInfo
     */
    @Override
    public void onPause(DownloadTaskInfo downloadInfo) {
        listAdapter.update(downloadInfo.getDownloadId(), DownloadConstant.STATUS_PAUSED);
    }

    /**
     * 更新下载
     *
     * @param downloadInfo
     * @param tatolSize
     * @param downloadSize
     */
    @Override
    public void update(DownloadTaskInfo downloadInfo, long tatolSize, long downloadSize) {
//        listAdapter.update(downloadId, tatolSize, downloadSize);
    }

    /**
     * 下载取消
     *
     * @param downloadInfo
     */
    @Override
    public void onCancel(DownloadTaskInfo downloadInfo) {
        listAdapter.remove(downloadInfo.getDownloadId());
    }

    /**
     * 下载完成
     *
     * @param downloadInfo
     * @param path
     */
    @Override
    public void onComplete(DownloadTaskInfo downloadInfo, String path) {
        listAdapter.update(downloadInfo.getDownloadId(), DownloadConstant.STATUS_COMPLETE);
    }

    /**
     * 下载出错
     *
     * @param downloadInfo
     * @param code
     * @param msg
     */
    @Override
    public void onFail(DownloadTaskInfo downloadInfo, int code, String msg) {
        listAdapter.update(downloadInfo.getDownloadId(), DownloadConstant.STATUS_ERROR);
    }

    class ManagerListAdapter extends BaseAdapter implements View.OnClickListener {

        List<DownloadTaskInfo> items;
        Map<Integer, View> maps = new HashMap();

        public ManagerListAdapter() {
            items = new ArrayList();
        }

        public void setItems(List<DownloadTaskInfo> data) {
            items = data;
            if (items == null)
                items = new ArrayList();
            notifyDataSetChanged();
        }

        public List<DownloadTaskInfo> getItems() {
            return items;
        }

        public void update(DownloadTaskInfo info) {
            int i = 0;
            for (; i < items.size(); i++) {
                if (items.get(i).getDownloadId().equals(info.getDownloadId())) {
                    break;
                }
            }
            if (i >= items.size())
                return;

            items.remove(i);
            items.add(i, info);
            notifyDataSetChanged();
        }

        public void update(String downloadId, int status) {
            for (DownloadTaskInfo info : items) {
                if (info.getDownloadId().equals(downloadId)) {
                    info.setStatus(status);
                }
            }
            notifyDataSetChanged();
        }

        public void update(String downloadId, int size, int downloadSize) {
            for (DownloadTaskInfo info : items) {
                if (info.getDownloadId().equals(downloadId)) {
                    info.setStatus(DownloadConstant.STATUS_DOWNLOADING);
                    info.setSize(size);
                    info.setDownloadSize(downloadSize);
                }
            }
            notifyDataSetChanged();
        }

        public void additem(DownloadTaskInfo data) {
            items.add(data);
        }

        public void remove(DownloadTaskInfo info) {
            Iterator<DownloadTaskInfo> iterator = items.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getDownloadId().equals(info.getDownloadId()))
                    iterator.remove();
            }
        }

        public void remove(String downloadId) {
            Iterator<DownloadTaskInfo> iterator = items.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getDownloadId().equals(downloadId))
                    iterator.remove();
            }
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public DownloadTaskInfo getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodler viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(DownloadManagerActivity.this).inflate(R.layout.download_manager_list_item, null);
                viewHolder = new ViewHodler();
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
                viewHolder.progressBar = (DownloadProgressBar) convertView.findViewById(R.id.list_item3_download_progress);
                viewHolder.btCancel = (Button) convertView.findViewById(R.id.bt_cancel);
                viewHolder.btStatus = (Button) convertView.findViewById(R.id.bt_status);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHodler) convertView.getTag();
            }
            DownloadTaskInfo item = getItem(position);

            Game game = new Gson().fromJson(item.getExtra(), Game.class);
            ImageLoader.getInstance().displayImage(game.getIcon(), viewHolder.ivIcon);
            viewHolder.tvTitle.setText(game.getName());
            viewHolder.tvDesc.setText(game.getDesc());

            viewHolder.btCancel.setTag(item);
            viewHolder.btCancel.setOnClickListener(this);
            viewHolder.btStatus.setTag(item);
            viewHolder.btStatus.setOnClickListener(this);
            viewHolder.progressBar.setData(item.getDownloadId(), item.getSize(), item.getDownloadSize());
            switch (item.getStatus()) {
                case DownloadConstant.STATUS_READY:
                    viewHolder.btStatus.setText("暂停");
                    break;
                case DownloadConstant.STATUS_PAUSED: {
                    viewHolder.btStatus.setText("开始");
                    break;
                }
                case DownloadConstant.STATUS_START:
                case DownloadConstant.STATUS_DOWNLOADING: {
                    viewHolder.btStatus.setText("暂停");
                    break;
                }
                case DownloadConstant.STATUS_CANCELLED:
                    viewHolder.btStatus.setText("开始");
                    break;
                case DownloadConstant.STATUS_COMPLETE: {
                    viewHolder.btStatus.setText("已完成");
                    break;
                }
                default:
                    viewHolder.btStatus.setText("重新下载");
                    break;
            }
            return convertView;
        }

        @Override
        public void onClick(View v) {
            DownloadTaskInfo item = (DownloadTaskInfo) v.getTag();
            Log.i(TAG, "onClick url:" + item.getUrl());
            if (v.getId() == R.id.bt_cancel) {
                DownloaderManager.getInstance().cancelTask(item.getDownloadId());
                items.remove(item);
                notifyDataSetChanged();
            } else if (v.getId() == R.id.bt_status) {
                switch (item.getStatus()) {
                    case DownloadConstant.STATUS_READY:
                        // 暂停下载
                        DownloaderManager.getInstance().pauseTask(item.getDownloadId());
                        break;
                    case DownloadConstant.STATUS_PAUSED:
                        // 重新下载
                        DownloaderManager.getInstance().resumeTask(item.getDownloadId());
                        break;
                    case DownloadConstant.STATUS_START:
                    case DownloadConstant.STATUS_DOWNLOADING:
                        // 暂停下载
                        DownloaderManager.getInstance().pauseTask(item.getDownloadId());
                        break;
                    case DownloadConstant.STATUS_CANCELLED:
                        break;
                    case DownloadConstant.STATUS_COMPLETE:
                        break;
                    default:
                        // 重新下载
                        DownloaderManager.getInstance().resumeTask(item.getDownloadId());
                        break;
                }
            }
        }

        class ViewHodler {
            ImageView ivIcon;
            TextView tvTitle, tvDesc;
            DownloadProgressBar progressBar;
            Button btCancel, btStatus;
        }
    }
}
