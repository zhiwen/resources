package com.resources.dal.mapper;

import com.resources.dal.module.MovieDO;

public interface MovieMapper {

    public long addMovie(MovieDO movieDO);

    public int deleteMovie(long resId);

    public int updateMovie(MovieDO movieDO);

    public MovieDO getMovie(long resId);
}
