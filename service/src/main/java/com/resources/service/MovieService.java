package com.resources.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.resources.common.Constant;
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

    public List<MovieDO> getMovieOrderByCreated(long cid, int country, long showTime, int offset, int limit) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("country", country);
        params.put("showTime", showTime);
        params.put("offset", Math.max(offset, 0));
        params.put("limit", Math.max(limit, 0));
        params.put(Constant.ORDERBY_COLUMNS, "modified_time");
        return movieMapper.getMovieOrderByColumn(params);
    }

}
