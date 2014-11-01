package com.resources.dal.module;

/**
 * 资源扩展信息表
 * 
 * @author zhiwenmizw
 */
public class ResourceInfoDO extends BaseDO {

    /**
     * 资源的标题
     */
    private String title;

    /**
     * 资源的描述信息 如电影描述、资源的描述等...
     */
    private String description;

    /**
     * 资源的分类
     */
    private long   cid;

    /**
     * 资源的类型(电影,图片，文库)
     */
    private int    bizType;

    /**
     * 资源的附件信息(JSON) 如 图片地址，资源地址...
     */
    private String attachment;

    /**
     * 浏览数
     */
    private long   viewCounts;

    /**
     * 下载数数
     */
    private long   downloadCounts;

    /**
     * 资源的状态（ok/no）
     */
    private int    status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public int getBizType() {
        return bizType;
    }

    public void setBizType(int bizType) {
        this.bizType = bizType;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public long getViewCounts() {
        return viewCounts;
    }

    public void setViewCounts(long viewCounts) {
        this.viewCounts = viewCounts;
    }

    public long getDownloadCounts() {
        return downloadCounts;
    }

    public void setDownloadCounts(long downloadCounts) {
        this.downloadCounts = downloadCounts;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
