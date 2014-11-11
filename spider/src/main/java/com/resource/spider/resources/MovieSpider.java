package com.resource.spider.resources;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.jsoup.nodes.Document;

import com.resource.spider.ResourceSpider;
import com.resources.dal.module.MovieDO;
import com.resources.dal.module.ResourceInfoDO;
import com.resources.service.MovieService;
import com.resources.service.ResourceInfoService;
import com.resources.service.dto.ResourceInfoDTO;

public abstract class MovieSpider extends ResourceSpider {

    @Resource
    private ResourceInfoService resourceInfoService;

    @Resource
    private MovieService        movieService;

    @Override
    public boolean parseDocument(Document document, Map<String, Object> userData) {

        ResourceInfoDTO<MovieDO> resourceInfoDTO = parseToMovie(document);

        MovieDO movieDO = resourceInfoDTO.getActualType();

        ResourceInfoDO resourceInfo = resourceInfoDTO.getResourceInfo();

        resourceInfo.setStatus(1);
        if (null == resourceInfo.getCreatedTime()) {
            resourceInfo.setCreatedTime(new Date());
            resourceInfo.setModifiedTime(resourceInfo.getCreatedTime());
        }

        long id = resourceInfoService.addResourceInfo(resourceInfo);
        if (id < 1) {
            throw new IllegalAccessError("add resource fail");
        }

        movieDO.setResId(resourceInfo.getId());

        if (null == movieDO.getCreatedTime()) {
            movieDO.setCreatedTime(new Date());
            movieDO.setModifiedTime(movieDO.getCreatedTime());
        }

        id = movieService.addMovie(movieDO);
        if (id < 1) {
            throw new IllegalAccessError("add movie fail");
        }
        return false;
    }

    public abstract ResourceInfoDTO<MovieDO> parseToMovie(Document document);
}
