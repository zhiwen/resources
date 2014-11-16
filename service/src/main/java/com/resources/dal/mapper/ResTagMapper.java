package com.resources.dal.mapper;

import java.util.List;

import com.resources.dal.dataobject.ResTagDO;

public interface ResTagMapper extends BaseMapper<ResTagDO, Long> {

    public List<ResTagDO> getDataByIds(List<Long> ids);
}
