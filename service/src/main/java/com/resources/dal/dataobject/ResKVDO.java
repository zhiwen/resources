package com.resources.dal.dataobject;

import com.resources.common.BaseDO;
import com.resources.common.ResKVTypeEnum;

public class ResKVDO extends BaseDO {

    private String        resKey;

    private String        resValue;

    private ResKVTypeEnum type;

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

    public ResKVTypeEnum getType() {
        return type;
    }

    public void setType(ResKVTypeEnum type) {
        this.type = type;
    }

}
