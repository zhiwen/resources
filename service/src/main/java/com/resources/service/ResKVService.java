package com.resources.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.resources.dal.dataobject.ResKVDO;
import com.resources.dal.mapper.ResKVMapper;

@Service
public class ResKVService {

    @Resource
    private ResKVMapper resKVMapper;

    public int addData(ResKVDO value) {
        if (null == value) {
            return 0;
        }
        ImmutableMap<String, String> param = ImmutableMap.of("resKey", value.getResKey(), "type",
                                                             value.getType().getValue());
        ResKVDO dbValue = resKVMapper.getDataByKeyWithType(param);
        if (null != dbValue) {
            value.setId(dbValue.getId());
            return 1;
        }

        return resKVMapper.addData(value);
    }

    public int delData(long pkId) {
        if (0 == pkId) {
            return 0;
        }
        return resKVMapper.delData(pkId);
    }

    public int updateData(ResKVDO value) {
        if (null == value || 0 == value.getId()) {
            return 0;
        }
        return resKVMapper.updateData(value);
    }

    public ResKVDO getData(long pkId) {
        if (0 == pkId) {
            return null;
        }
        return resKVMapper.getData(pkId);
    }
}
