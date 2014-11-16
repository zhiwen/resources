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
        return resMovieMapper.addMovie(movieDO);
    }

    public int addMovie(ResMovieDO movieDO) {
        return resMovieMapper.addMovie(movieDO);
    }

    public int deleteMovie(long id) {
        return resMovieMapper.deleteMovie(id);
    }

    public int updateMovie(ResMovieDO movieDO) {
        return resMovieMapper.updateMovie(movieDO);
    }

    public ResMovieDO getMovieByDid(long did) {
        return resMovieMapper.getMovieByDid(did);
    }

    public ResMovieDO getMovie(long id) {
        return resMovieMapper.getMovie(id);
    }

    public List<ResMovieDO> getMovieByPaginatorWithStatus(int dataStatus, int offset, int length) {
        ImmutableMap<String, Integer> params = ImmutableMap.of("dataStatus", dataStatus, "offset", offset, "length",
                                                               length);
        return resMovieMapper.getMovieByPaginatorWithStatus(params);
    }

}
