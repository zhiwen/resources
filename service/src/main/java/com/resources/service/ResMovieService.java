package com.resources.service;

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

    public int addMovie(ResMovieDO movieDO) {
        return resMovieMapper.addMovie(movieDO);
    }

    public int deleteMovie(long id) {
        return resMovieMapper.deleteMovie(id);
    }

    public int updateMovie(ResMovieDO movieDO) {
        return resMovieMapper.updateMovie(movieDO);
    }

    public ResMovieDO getMovie(long id) {
        return resMovieMapper.getMovie(id);
    }

    public List<ResMovieDO> getMovieByPaginator(int offset, int length) {
        ImmutableMap<String, Integer> params = ImmutableMap.of("offset", offset, "length", length);
        return resMovieMapper.getMovieByPaginator(params);
    }

}
