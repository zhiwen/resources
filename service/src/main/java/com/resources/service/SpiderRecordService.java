package com.resources.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.resources.dal.dataobject.SpiderRecordDO;

@Service
public class SpiderRecordService {

    @Resource
    private SpiderRecordService spiderRecordService;

    public int addRecord(SpiderRecordDO recordDO) {
        return spiderRecordService.addRecord(recordDO);
    }

    public int deleteRecord(long id) {
        return spiderRecordService.deleteRecord(id);
    }

    public int updateRecord(SpiderRecordDO recordDO) {
        return spiderRecordService.updateRecord(recordDO);
    }

    public List<SpiderRecordDO> getRecordByType(int type) {
        return spiderRecordService.getRecordByType(type);
    }

}
