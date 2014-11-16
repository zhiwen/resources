package com.resources.dal.dataobject;

import com.resources.common.BaseDO;
import com.resources.common.BizTypeEnum;

public class ResTagDO extends BaseDO {

    private long        cid;
    private BizTypeEnum bizType;
    private String      tagName;

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
