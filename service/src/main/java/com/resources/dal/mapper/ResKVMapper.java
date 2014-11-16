package com.resources.dal.mapper;

import java.util.Map;

import com.resources.dal.dataobject.ResKVDO;

public interface ResKVMapper extends BaseMapper<ResKVDO, Long> {

    public ResKVDO getDataByKeyWithType(Map<String, ? extends Object> param);
}
