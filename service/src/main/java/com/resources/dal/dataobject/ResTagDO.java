package com.resources.dal.dataobject;

import com.resources.common.BaseDO;

public class ResTagDO extends BaseDO {

    private long   cid;
    private int    bizType;
    private String tagName;

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

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

}
