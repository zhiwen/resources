package com.resources.dal.module;

import java.util.Date;

public class SpiderResourcesDO {

    private String name;

    private String cleanedName;

    private String url;

    private String downloadUrl;

    private String doubanId;

    private Date   createdTime;

    private int    status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCleanedName() {
        return cleanedName;
    }

    public void setCleanedName(String cleanedName) {
        this.cleanedName = cleanedName;
    }

    public String getDoubanId() {
        return doubanId;
    }

    public void setDoubanId(String doubanId) {
        this.doubanId = doubanId;
    }

}
