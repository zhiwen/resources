package com.resources.service.dto;

import com.resources.dal.module.ResourceInfoDO;

public class ResourceInfoDTO<T> {

    private ResourceInfoDO resourceInfo;

    private T              actualType;

    public ResourceInfoDTO(){
    }

    public ResourceInfoDTO(ResourceInfoDO resourceInfo, T actualType){
        super();
        this.resourceInfo = resourceInfo;
        this.actualType = actualType;
    }

    public ResourceInfoDO getResourceInfo() {
        return resourceInfo;
    }

    public void setResourceInfo(ResourceInfoDO resourceInfo) {
        this.resourceInfo = resourceInfo;
    }

    public T getActualType() {
        return actualType;
    }

    public void setActualType(T actualType) {
        this.actualType = actualType;
    }

}
