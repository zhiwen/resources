package com.resources.common;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class BaseDO {

    private Date createdTime;

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
