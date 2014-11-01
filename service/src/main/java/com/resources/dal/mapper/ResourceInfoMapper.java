package com.resources.dal.mapper;

import java.util.List;

import com.resources.dal.module.ResourceInfoDO;

public interface ResourceInfoMapper {

    public long addResourceInfo(ResourceInfoDO resourceInfo);

    public int deleteResourceInfo(long resId);

    public int updateResourceInfo(ResourceInfoDO resourceInfo);

    public ResourceInfoDO getResourceInfo(long resId);

    public List<ResourceInfoDO> getResourceInfoByIds(List<Long> ids);

    /**
     * 获取按浏览数倒序的资源
     * 
     * @param cid
     * @param bizType
     * @param offset
     * @param limit
     * @return
     */
    public List<ResourceInfoDO> getOrderResourceInfoByViews(long cid, long bizType, int offset, int limit);

    /**
     * 获取按下载数倒序的资源
     * 
     * @param cid
     * @param bizType
     * @param offset
     * @param limit
     * @return
     */
    public List<ResourceInfoDO> getOrderResourceInfoByDownloads(long cid, long bizType, int offset, int limit);

}
