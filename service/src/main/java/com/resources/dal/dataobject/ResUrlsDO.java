package com.resources.dal.dataobject;

import com.resources.common.BaseDO;

public class ResUrlsDO extends BaseDO {

    private long   id;

    /**
     * 业务类型（预告片、播放地址、下载地址、封面、剧照）
     */
    private int    bizType;
    /**
     * url
     */
    private String url;
    /**
     * 简单描述，如一张图片的描述
     */
    private String description;
    /**
     * 对象id，为任何类型的id（电影或文库）
     */
    private long   objectId;
}
