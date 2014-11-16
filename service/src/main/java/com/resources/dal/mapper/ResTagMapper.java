package com.resources.dal.mapper;

import java.util.List;
import java.util.Map;

import com.resources.dal.dataobject.ResTagDO;

public interface ResTagMapper extends BaseMapper<ResTagDO, Long> {

    public ResTagDO getDataByNameWithType(Map<String, ? extends Object> param);

    public List<ResTagDO> getDataByIds(List<Long> ids);
}
