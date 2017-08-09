package com.download.notification;

import android.app.Notification;
import android.support.v7.app.NotificationCompat;

import com.download.R;
import com.download.DownloaderManager;
import com.download.constants.DownloadConstant;
import com.download.util.DownloadUtils;

/**
 * Created by yangtianfei on 17-6-29.
 */
public class NotificationItem extends BaseNotificationItem {

    //    PendingIntent pendingIntent;
    NotificationCompat.Builder builder;

    public NotificationItem(int id, String title, String desc) {
        super(id, title, desc);
//        Intent[] intents = new Intent[2];
//        intents[0] = Intent.makeMainActivity(new ComponentName(YYDownloadManager.getInstance().getContext(), MainActivity.class));
//        intents[1] = new Intent(YYDownloadManager.getInstance().getContext(), NotificationSampleActivity.class);

//        this.pendingIntent = PendingIntent.getActivities(YYDownloadManager.getInstance().getContext(), 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(DownloaderManager.getInstance().getConfiguration().mContext);

        builder.setDefaults(Notification.DEFAULT_LIGHTS)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle(getTitle())
                .setContentText(desc)
//                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
    }

    @Override
    public void show(boolean statusChanged, int status, boolean isShowProgress) {
        String desc = getDesc();
        switch (status) {
            case DownloadConstant.STATUS_READY:
                desc += DownloaderManager.getInstance().getConfiguration().mContext.getString(R.string.download_status_wait);
                break;
            case DownloadConstant.STATUS_START:
                desc += DownloaderManager.getInstance().getConfiguration().mContext.getString(R.string.download_status_start);
                break;
            case DownloadConstant.STATUS_PAUSED:
                desc += DownloaderManager.getInstance().getConfiguration().mContext.
                        getString(R.string.download_size_desc, DownloadUtils.getTotalSize(getTotal()), DownloadUtils.getDownloadSize(getSofar(), getTotal()));
                break;
            case DownloadConstant.STATUS_DOWNLOADING:
                desc += DownloaderManager.getInstance().getConfiguration().mContext.
                        getString(R.string.download_size_desc, DownloadUtils.getTotalSize(getTotal()), DownloadUtils.getDownloadSize(getSofar(), getTotal()));
                break;
            case DownloadConstant.STATUS_CANCELLED:
                desc += DownloaderManager.getInstance().getConfiguration().mContext.getString(R.string.download_status_cancel);
                break;
            case DownloadConstant.STATUS_COMPLETE:
                desc += DownloaderManager.getInstance().getConfiguration().mContext.getString(R.string.download_status_complete);
                break;
            case DownloadConstant.STATUS_ERROR:
                desc += DownloaderManager.getInstance().getConfiguration().mContext.getString(R.string.download_status_fail);
                break;
        }

        builder.setContentTitle(getTitle()).setContentText(desc);

        if (statusChanged) {
            builder.setTicker(desc);
        }
        int progress = 0;
        if (getTotal() > 0)
            progress = (int) (getSofar() * 100 / getTotal());
        builder.setProgress(100, progress, !isShowProgress);
        getManager().notify(getId(), builder.build());
    }
}
