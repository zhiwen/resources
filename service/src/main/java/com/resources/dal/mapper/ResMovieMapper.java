package com.resources.dal.mapper;

import java.util.List;
import java.util.Map;

import com.resources.dal.dataobject.ResMovieDO;

public interface ResMovieMapper extends BaseMapper<ResMovieDO, Long> {

    public ResMovieDO getDataByDid(long did);

    public List<ResMovieDO> getMovieByPaginatorWithStatus(Map<String, ? extends Object> params);

}
