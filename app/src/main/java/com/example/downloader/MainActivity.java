package com.example.downloader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.download.DownloaderConfiguration;
import com.download.DownloaderManager;
import com.example.downloader.bean.Game;
import com.example.downloader.download.InstallDownloadListener;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    ListView lv;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_manager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DownloadManagerActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.bt_manager2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DownloadManagerWithCustomItemViewActivity.class);
                startActivity(intent);
            }
        });
        lv = (ListView) findViewById(R.id.list);
        listAdapter = new ListAdapter();
        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Game game = listAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, DownloadDetailActivity.class);
                intent.putExtra("extra", new Gson().toJson(game));
                startActivity(intent);
            }
        });

        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);

        // init download
        DownloaderConfiguration config = new DownloaderConfiguration.Builder(this)
                .downloadDir(getExternalFilesDir(null).getAbsolutePath() + File.separator)
                .isDownloadOnlyOnWifi(false)
                .isNotifition(true)
                .defaultTaskListener(new InstallDownloadListener(this)).build();
        DownloaderManager.getInstance().init(config);
    }

    class ListAdapter extends BaseAdapter {

        List<Game> games;

        public ListAdapter() {
            games = new ArrayList();
            Game game = new Game();
            game.setGid("10");
            game.setIcon("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
            game.setName("k");
            game.setDesc("下载游戏");
            game.setUrl("http://dl.download.csdn.net/down10/20141006/cb8950d9d24005359904d602b521e5df.zip?response-content-disposition=attachment%3Bfilename%3D%22java%E5%B9%B6%E5%8F%91%E7%BC%96%E7%A8%8B%E5%AE%9E%E6%88%98pdf%E5%8F%8A%E6%BA%90%E7%A0%81.zip%22&OSSAccessKeyId=9q6nvzoJGowBj4q1&Expires=1496738787&Signature=RP%2Bf%2FEJv4Zbe030mPioDqEuGB%2B0%3D");
            games.add(game);
            game = new Game();
            game.setGid("1");
            game.setIcon("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
            game.setName("a");
            game.setDesc("下载游戏");
            game.setUrl("http://download.xitongxz.com/Ylmf_Ghost_Win7_SP1_x64_2017_0113.iso");
            games.add(game);
            game = new Game();
            game.setGid("2");
            game.setIcon("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
            game.setName("b");
            game.setDesc("下载游戏。");
            game.setUrl("https://bitbucket.org/iBotPeaches/apktool/get/a918b49bff53.zip");
            games.add(game);
            game = new Game();
            game.setGid("3");
            game.setIcon("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
            game.setName("c");
            game.setDesc("下载游戏");
            game.setUrl("https://nj01ct01.baidupcs.com/file/9364c5d9e85a90defc70a308872e6dca?bkt=p3-0000fefe53f8143a20bff52be296615e6ea4&fid=2483678418-250528-587681434491657&time=1495854698&sign=FDTAXGERLBHS-DCb740ccc5511e5e8fedcff06b081203-h9Wz7ugc9Y7XOLNltilHEAR0SRM%3D&to=63&size=2819280456&sta_dx=2819280456&sta_cs=0&sta_ft=zip&sta_ct=5&sta_mt=4&fm2=MH,Yangquan,Netizen-anywhere,,beijing,ct&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=0000fefe53f8143a20bff52be296615e6ea4&sl=83034191&expires=8h&rt=pr&r=971691078&mlogid=3396969566242768868&vuk=2483678418&vbdid=2611537661&fin=android.zip&fn=android.zip&rtype=1&iv=0&dp-logid=3396969566242768868&dp-callid=0.1.1&hps=1&csl=300&csign=YSAppgLaUxkXEu5aDvnJP39%2F0xM%3D&by=themis");
            games.add(game);
            game = new Game();
            game.setGid("4");
            game.setIcon("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
            game.setName("d");
            game.setDesc("下载游戏");
            game.setUrl("http://ugame.9game.cn/game/downloadGame?pack.cooperateModelId=46978&pack.id=10691666");
            games.add(game);
            game = new Game();
            game.setGid("5");
            game.setIcon("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
            game.setName("e");
            game.setDesc("下载游戏");
            game.setUrl("http://123.125.110.23/imtt.dd.qq.com/16891/AA66E38AF73AD9CC9205975131DF6003.apk?mkey=59187c3efd96fb28&f=9512&c=0&fsname=com.baidu.netdisk_7.17.0_516.apk&csr=2097&_track_d99957f7=98a3c14d-638b-47cb-8ad7-db1f1f34905a&p=.apk");
            games.add(game);
            game = new Game();
            game.setGid("6");
            game.setIcon("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
            game.setName("f");
            game.setDesc("下载游戏");
            game.setUrl("https://os.alipayobjects.com/rmsportal/QzHtAdtQsiszefrhluMR.zip");
            games.add(game);
            game = new Game();
            game.setGid("7");
            game.setIcon("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
            game.setName("g");
            game.setDesc("下载游戏");
            game.setUrl("http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jdk-8u131-linux-x64.tar.gz?AuthParam=1492845588_64d002d8c6c0f43ebe51c05958f04936");
            games.add(game);
            game = new Game();
            game.setGid("8");
            game.setIcon("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
            game.setName("h");
            game.setDesc("下载游戏");
            game.setUrl("http://123.125.110.23/imtt.dd.qq.com/16891/AA66E38AF73AD9CC9205975131DF6003.apk?mkey=59187c3efd96fb28&f=9512&c=0&fsname=com.baidu.netdisk_7.17.0_516.apk&csr=2097&_track_d99957f7=98a3c14d-638b-47cb-8ad7-db1f1f34905a&p=.apk");
            games.add(game);
            game = new Game();
            game.setGid("9");
            game.setIcon("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
            game.setName("i");
            game.setDesc("下载游戏");
            game.setUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1494943132664&di=847f1556a502f407006b210a09edf0ef&imgtype=0&src=http%3A%2F%2Fwww.bz55.com%2Fuploads%2Fallimg%2F120709%2F1-120F9100G6.jpg");
            games.add(game);
        }

        @Override
        public int getCount() {
            return games.size();
        }

        @Override
        public Game getItem(int position) {
            return games.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.download_main_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Game game = getItem(position);
            ImageLoader.getInstance().displayImage(game.getIcon(), viewHolder.ivIcon);
            viewHolder.tvTitle.setText(game.getName());
            viewHolder.tvDesc.setText(game.getDesc());
            return convertView;
        }

        class ViewHolder {
            ImageView ivIcon;
            TextView tvTitle;
            TextView tvDesc;
        }
    }
}
