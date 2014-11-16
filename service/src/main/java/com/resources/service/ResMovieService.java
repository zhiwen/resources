package com.resources.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.resources.dal.dataobject.ResMovieDO;
import com.resources.dal.mapper.ResMovieMapper;

@Service
public class ResMovieService {

    @Resource
    private ResMovieMapper resMovieMapper;

    public int addMovieList(List<Long> didList) {
        int count = 0;
        for (Long did : didList) {
            count += addMovie(did);
        }
        return count;
    }

    public int addMovie(Long did) {

        ResMovieDO movieDO = getMovieByDid(did);
        if (null != movieDO) {
            return 0;
        }
        movieDO = new ResMovieDO();
        movieDO.setDid(did);
        movieDO.setTitle("");
        movieDO.setCreatedTime(new Date());
        movieDO.setModifiedTime(movieDO.getCreatedTime());
        return resMovieMapper.addData(movieDO);
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

    public ResMovieDO getMovie(long id) {
        return resMovieMapper.getData(id);
    }

    public List<ResMovieDO> getMovieByPaginatorWithStatus(int dataStatus, int offset, int length) {
        ImmutableMap<String, Integer> params = ImmutableMap.of("dataStatus", dataStatus, "offset", offset, "length",
                                                               length);
        return resMovieMapper.getMovieByPaginatorWithStatus(params);
    }

}
