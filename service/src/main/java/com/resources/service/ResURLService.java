package com.resources.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.resources.dal.dataobject.ResURLDO;
import com.resources.dal.mapper.ResURLMapper;

@Service
public class ResURLService {

    @Resource
    private ResURLMapper resURLMapper;

    public int addData(ResURLDO value) {
        if (null == value) {
            return 0;
        }
        return resURLMapper.addData(value);
    }

    public int delData(long pkId) {
        if (0 == pkId) {
            return 0;
        }
        return resURLMapper.delData(pkId);
    }

    public int updateData(ResURLDO value) {
        if (null == value || 0 == value.getId()) {
            return 0;
        }
        return resURLMapper.updateData(value);
    }

    public ResURLDO getData(long pkId) {
        if (0 == pkId) {
            return null;
        }
        return resURLMapper.getData(pkId);
    }
}
