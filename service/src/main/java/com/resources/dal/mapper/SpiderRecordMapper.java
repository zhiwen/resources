package com.resources.dal.mapper;

import java.util.List;

import com.resources.dal.dataobject.SpiderRecordDO;

public interface SpiderRecordMapper {

    public int addRecord(SpiderRecordDO recordDO);

    public int deleteRecord(long id);

    public int updateRecord(SpiderRecordDO recordDO);

    public List<SpiderRecordDO> getRecordByType(int type);

}
