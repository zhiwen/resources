package com.resources.dal.mapper;

import java.util.List;
import java.util.Map;

import com.resources.dal.module.SpiderResourcesDO;

public interface SpiderResourcesMapper {

    public int addSpiderResources(SpiderResourcesDO spiderRes);

    public int updateSpiderResources(SpiderResourcesDO spiderRes);

    public List<SpiderResourcesDO> getSpiderResources(Map<String, Integer> params);

}
