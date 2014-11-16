package com.resources.dal.dataobject;

import com.resources.common.BaseDO;

public class ResKVDO extends BaseDO {

    private String resKey;

    private String resValue;

    private String type;

    public String getResKey() {
        return resKey;
    }

    public void setResKey(String resKey) {
        this.resKey = resKey;
    }

    public String getResValue() {
        return resValue;
    }

    public void setResValue(String resValue) {
        this.resValue = resValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
