package com.resources.dal.dataobject;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class SpiderRecordDO {

    private long   id;

    private int    type;

    private String tagName;

    private String spiderInfo;

    private Date   spiderDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getSpiderInfo() {
        return spiderInfo;
    }

    public void setSpiderInfo(String spiderInfo) {
        this.spiderInfo = spiderInfo;
    }

    public Date getSpiderDate() {
        return spiderDate;
    }

    public void setSpiderDate(Date spiderDate) {
        this.spiderDate = spiderDate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static enum SpiderRecordTypeEnum {

        douban_movie(1), douban_tv(2);

        private final int value;

        private SpiderRecordTypeEnum(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

}
