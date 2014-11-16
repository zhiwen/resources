package com.resources.dal.dataobject;

import com.resources.common.BaseDO;

public class SpiderRecordDO extends BaseDO {

    private int    type;

    private String tagName;

    private int    pageNumber;

    private int    eatNumber;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getEatNumber() {
        return eatNumber;
    }

    public void setEatNumber(int eatNumber) {
        this.eatNumber = eatNumber;
    }

    public static enum SpiderRecordTypeEnum {

        douban(1);

        private final int value;

        private SpiderRecordTypeEnum(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

}
