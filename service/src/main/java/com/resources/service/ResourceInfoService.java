package com.resources.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.resources.common.BizTypeEnum;
import com.resources.common.Constant;
import com.resources.dal.mapper.ResourceInfoMapper;
import com.resources.dal.module.ResourceInfoDO;

@Service
public class ResourceInfoService {

    @Resource
    private ResourceInfoMapper resourceInfoMapper;

    public long addResourceInfo(ResourceInfoDO resourceInfo) {
        return resourceInfoMapper.addResourceInfo(resourceInfo);
    }

    public int deleteResourceInfo(long resId) {
        return resourceInfoMapper.deleteResourceInfo(resId);
    }

    public int updateResourceInfo(ResourceInfoDO resourceInfo) {
        return resourceInfoMapper.updateResourceInfo(resourceInfo);
    }

    public ResourceInfoDO getResourceInfo(long resId) {
        if (resId > 0) {
            return resourceInfoMapper.getResourceInfo(resId);
        }
        return null;
    }

    public List<ResourceInfoDO> getResourceInfoByIds(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            return resourceInfoMapper.getResourceInfoByIds(ids);
        }
        return Collections.emptyList();
    }

    /**
     * 获取按浏览数倒序的资源
     * 
     * @param cid
     * @param BizTypeEnum
     * @param offset
     * @param limit
     * @return
     */
    public List<ResourceInfoDO> getOrderResourceInfoByViews(long cid, BizTypeEnum BizTypeEnum, int offset, int limit) {

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cid", cid);
        params.put("BizTypeEnum", BizTypeEnum.getValue());
        params.put("offset", offset);
        params.put("limit", limit);
        params.put(Constant.ORDERBY_COLUMNS, "view_counts");
        return resourceInfoMapper.getOrderResourceInfoByColumns(params);
    }

    /**
     * 获取按下载数倒序的资源
     * 
     * @param cid
     * @param BizTypeEnum
     * @param offset
     * @param limit
     * @return
     */
    public List<ResourceInfoDO> getOrderResourceInfoByDownloads(long cid, BizTypeEnum BizTypeEnum, int offset, int limit) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cid", cid);
        params.put("BizTypeEnum", BizTypeEnum.getValue());
        params.put("offset", offset);
        params.put("limit", limit);
        params.put(Constant.ORDERBY_COLUMNS, "download_counts");
        return resourceInfoMapper.getOrderResourceInfoByColumns(params);
    }

    public List<ResourceInfoDO> getOrderResourceInfoByCreated(long cid, BizTypeEnum BizTypeEnum, int offset, int limit) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("cid", cid);
        params.put("BizTypeEnum", BizTypeEnum.getValue());
        params.put("offset", offset);
        params.put("limit", limit);
        params.put(Constant.ORDERBY_COLUMNS, "modified_time");
        return resourceInfoMapper.getOrderResourceInfoByColumns(params);
    }
}
