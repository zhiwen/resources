package com.resources.dal.mapper;

import java.util.List;
import java.util.Map;

import com.resources.dal.dataobject.ResMovieDO;

public interface ResMovieMapper {

    public int addMovie(ResMovieDO movieDO);

    public int deleteMovie(long id);

    public int updateMovie(ResMovieDO movieDO);

    public ResMovieDO getMovieByDid(long did);

    public ResMovieDO getMovie(long id);

    public List<ResMovieDO> getMovieByPaginator(Map<String, ? extends Object> params);

}
