package com.resources.service;

import com.resources.dal.module.MovieDO;

public interface MovieService {

    public long addMovie(MovieDO movieDO);

    public long deleteMovie(long resId);

    public int updateMovie(MovieDO movieDO);

    public MovieDO getMovie(long resId);
}
