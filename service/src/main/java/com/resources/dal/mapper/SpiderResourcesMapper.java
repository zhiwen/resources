package com.resources.dal.mapper;

import com.resources.dal.module.SpiderResourcesDO;

public interface SpiderResourcesMapper {

    public long addSpiderResources(SpiderResourcesDO spiderRes);

    public SpiderResourcesDO getSpiderResources(String name);

}
