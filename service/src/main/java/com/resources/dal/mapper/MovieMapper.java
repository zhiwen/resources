package com.resources.dal.mapper;

import java.util.List;
import java.util.Map;

import com.resources.dal.module.MovieDO;

public interface MovieMapper {

    public long addMovie(MovieDO movieDO);

    public int deleteMovie(long resId);

    public int updateMovie(MovieDO movieDO);

    public MovieDO getMovie(long resId);

    public List<MovieDO> getMovieOrderByColumn(Map<String, Object> params);
}
