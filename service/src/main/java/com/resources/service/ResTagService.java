package com.resources.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.resources.dal.dataobject.ResTagDO;
import com.resources.dal.mapper.ResTagMapper;

@Service
public class ResTagService {

    @Resource
    private ResTagMapper resTagMapper;

    public int addData(ResTagDO value) {
        if (null == value) {
            return 0;
        }
        return resTagMapper.addData(value);
    }

    public int addDataIfNotExist(ResTagDO value) {
        if (null == value || 0 == value.getId()) {
            return 0;
        }
        ResTagDO dbTagDO = resTagMapper.getData(value.getId());
        if (null != dbTagDO) {
            return 1;
        }
        return resTagMapper.addData(value);
    }

    public int delData(long pkId) {
        if (0 == pkId) {
            return 0;
        }
        return resTagMapper.delData(pkId);
    }

    public int updateData(ResTagDO value) {
        if (null == value || 0 == value.getId()) {
            return 0;
        }
        return resTagMapper.updateData(value);
    }

    public ResTagDO getData(long pkId) {
        if (0 == pkId) {
            return null;
        }
        return resTagMapper.getData(pkId);
    }

    public List<ResTagDO> getTagListByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return resTagMapper.getDataByIds(ids);
    }
}
