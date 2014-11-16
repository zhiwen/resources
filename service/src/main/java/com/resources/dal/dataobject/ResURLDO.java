package com.resources.dal.dataobject;

import com.resources.common.BaseDO;
import com.resources.common.BizTypeEnum;

public class ResURLDO extends BaseDO {

    /**
     * 业务类型（预告片、播放地址、下载地址、封面、剧照）
     */
    private BizTypeEnum bizType;
    /**
     * url
     */
    private String      url;
    /**
     * 简单描述，如一张图片的描述
     */
    private String      description;
    /**
     * 对象id，为任何类型的id（电影或文库）
     */
    private long        objectId;

    public BizTypeEnum getBizType() {
        return bizType;
    }

    public void setBizType(BizTypeEnum bizType) {
        this.bizType = bizType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

}
