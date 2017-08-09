package com.example.downloader.bean;

/**
 * Created by yangtianfei on 17-6-27.
 */
public class Game {
    private String gid;
    private String icon;
    private String name;
    private String desc;
    private String url;
    private boolean isSupport;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSupport() {
        return isSupport;
    }

    public void setSupport(boolean support) {
        isSupport = support;
    }
}
