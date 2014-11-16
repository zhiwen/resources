package com.resources.dal.dataobject;

import java.io.Serializable;

import com.resources.common.BaseDO;
import com.resources.common.BizTypeEnum;

public class ResTagDO extends BaseDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3769856614338870951L;

    private long              cid;
    private BizTypeEnum       bizType;
    private String            tagName;

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public BizTypeEnum getBizType() {
        return bizType;
    }

    public void setBizType(BizTypeEnum bizType) {
        this.bizType = bizType;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

}
