package com.resources.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

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
     * @param bizType
     * @param offset
     * @param limit
     * @return
     */
    public List<ResourceInfoDO> getOrderResourceInfoByViews(long cid, long bizType, int offset, int limit) {
        return null;
    }

    /**
     * 获取按下载数倒序的资源
     * 
     * @param cid
     * @param bizType
     * @param offset
     * @param limit
     * @return
     */
    public List<ResourceInfoDO> getOrderResourceInfoByDownloads(long cid, long bizType, int offset, int limit) {
        return null;
    }
}
