package com.resources.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.resources.dal.mapper.SpiderResourcesMapper;
import com.resources.dal.module.SpiderResourcesDO;

@Service
public class SpiderResourcesService {

    @Resource
    private SpiderResourcesMapper spiderResourcesMapper;

    public long addSpiderResources(SpiderResourcesDO spiderRes) {
        return spiderResourcesMapper.addSpiderResources(spiderRes);
    }

    public SpiderResourcesDO getSpiderResources(String name) {
        return spiderResourcesMapper.getSpiderResources(name);
    }
}
