package com.download.task;

/**
 * Created by yangtianfei on 17-5-15.
 */
public class DownloadTaskInfo {
    private long id;
    /**
     * 下载id
     */
    private String downloadId;
    /**
     * 下载类型
     */
    private int type;
    /**
     * 下载title
     */
    private String title;
    /**
     * 下载url
     */
    private String url;
    /**
     * 下载文件大小
     */
    private long size = -1;
    /**
     * 下载进度
     */
    private long downloadSize;
    /**
     * 下载路径
     */
    private String filePath;
    /**
     * 下载额外信息
     */
    private String extra;
    private long createTime;
    /**
     * 下载状态
     */
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
