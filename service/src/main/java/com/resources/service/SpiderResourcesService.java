package com.resources.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.resources.dal.mapper.SpiderResourcesMapper;
import com.resources.dal.module.SpiderResourcesDO;

@Service
public class SpiderResourcesService {

    @Resource
    private SpiderResourcesMapper spiderResourcesMapper;

    public int addSpiderResources(SpiderResourcesDO spiderRes) {
        return spiderResourcesMapper.addSpiderResources(spiderRes);
    }

    public int updateSpiderResources(SpiderResourcesDO spiderRes) {
        return spiderResourcesMapper.updateSpiderResources(spiderRes);
    }

    public List<SpiderResourcesDO> getSpiderResources(int offset, int length) {
        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("offset", offset);
        params.put("length", length);
        return spiderResourcesMapper.getSpiderResources(params);
    }

    public SpiderResourcesDO getSpiderResource(String name) {
        return spiderResourcesMapper.getSpiderResource(name);
    }

    public SpiderResourcesDO getSpiderResourceById(long id) {
        return spiderResourcesMapper.getSpiderResourceById(id);
    }
}
