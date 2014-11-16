package com.resources.dal.mapper;

import java.util.List;

import com.resources.dal.dataobject.SpiderRecordDO;

public interface SpiderRecordMapper extends BaseMapper<SpiderRecordDO, Long> {

    public List<SpiderRecordDO> getRecordByType(int type);

}
