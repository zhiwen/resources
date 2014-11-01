package com.resources.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.resources.dal.mapper.MovieMapper;
import com.resources.dal.module.MovieDO;

@Service
public class MovieService {

    @Resource
    private MovieMapper movieMapper;

    public long addMovie(MovieDO movieDO) {
        return movieMapper.addMovie(movieDO);
    }

    public int deleteMovie(long resId) {
        return movieMapper.deleteMovie(resId);
    }

    public int updateMovie(MovieDO movieDO) {
        return movieMapper.updateMovie(movieDO);
    }

    public MovieDO getMovie(long resId) {
        return movieMapper.getMovie(resId);
    }
}
