package com.example.downloader;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.download.DownloaderManager;
import com.download.task.DownloadTaskInfo;
import com.example.downloader.ui.DownloadItemView;

import java.util.ArrayList;
import java.util.List;

public class DownloadManagerWithCustomItemViewActivity extends AppCompatActivity {
    private final String TAG = DownloadManagerWithCustomItemViewActivity.class.getSimpleName();

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
    }

    class ManagerListAdapter extends BaseAdapter {

        List<DownloadTaskInfo> items;

        public ManagerListAdapter() {
            items = new ArrayList();
        }

        public void setItems(List<DownloadTaskInfo> data) {
            items = data;
            if (items == null)
                items = new ArrayList();
            notifyDataSetChanged();
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
            DownloadItemView view;
            if (convertView == null) {
//                convertView = LayoutInflater.from(DownloadManagerActivity.this).inflate(R.layout.list_item_download, null);
                convertView = view = new DownloadItemView(DownloadManagerWithCustomItemViewActivity.this);
            } else {
                view = (DownloadItemView) convertView;
            }
            DownloadTaskInfo item = getItem(position);
            view.setDownloadInfo(item);
            return convertView;
        }
    }
}
