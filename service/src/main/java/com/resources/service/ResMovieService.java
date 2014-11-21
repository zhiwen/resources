package com.resources.service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.resources.dal.dataobject.ResMovieDO;
import com.resources.dal.mapper.ResMovieMapper;

@Service
public class ResMovieService {

    @Resource
    private ResMovieMapper resMovieMapper;

    public int addMovieList(List<Long> didList, int dataStatus) {

        List<ResMovieDO> resMovieList = resMovieMapper.getDataByDidList(didList);
        Map<Long, ResMovieDO> resMovieIdMapping = new HashMap<Long, ResMovieDO>();
        for (ResMovieDO resMovieDO : resMovieList) {
            resMovieIdMapping.put(resMovieDO.getDid(), resMovieDO);
        }

        int count = 0;
        for (Long did : didList) {
            ResMovieDO movieDO = resMovieIdMapping.get(did);
            if (null != movieDO) {
                continue;
            }
            movieDO = new ResMovieDO();
            movieDO.setDid(did);
            movieDO.setTitle("");
            movieDO.setDataStatus(dataStatus);
            movieDO.setCreatedTime(new Date());
            movieDO.setModifiedTime(movieDO.getCreatedTime());
            count += resMovieMapper.addData(movieDO);

        }
        return count;
    }

    public int addMovie(ResMovieDO movieDO) {
        return resMovieMapper.addData(movieDO);
    }

    public int deleteMovie(long id) {
        return resMovieMapper.delData(id);
    }

    public int updateMovie(ResMovieDO movieDO) {
        return resMovieMapper.updateData(movieDO);
    }

    public ResMovieDO getMovieByDid(long did) {
        return resMovieMapper.getDataByDid(did);
    }

    public List<ResMovieDO> getDataByDidList(List<Long> dids) {
        if (CollectionUtils.isEmpty(dids)) {
            return Collections.emptyList();
        }
        return resMovieMapper.getDataByDidList(dids);
    }

    public ResMovieDO getMovie(long id) {
        return resMovieMapper.getData(id);
    }

    public List<ResMovieDO> getMovieByPaginatorWithStatus(int dataStatus, int offset, int length) {
        ImmutableMap<String, Integer> params = ImmutableMap.of("dataStatus", dataStatus, "offset", offset, "length",
                                                               length);
        return resMovieMapper.getMovieByPaginatorWithStatus(params);
    }

}
