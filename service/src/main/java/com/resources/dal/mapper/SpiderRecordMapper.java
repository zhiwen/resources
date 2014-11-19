package com.resources.dal.mapper;

import java.util.List;
import java.util.Map;

import com.resources.dal.dataobject.SpiderRecordDO;

public interface SpiderRecordMapper extends BaseMapper<SpiderRecordDO, Long> {

    public List<SpiderRecordDO> getRecordByType(int type);

    public SpiderRecordDO getRecordByTypeWithName(Map<String, ? extends Object> param);

}
