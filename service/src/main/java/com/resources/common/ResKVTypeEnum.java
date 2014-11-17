package com.resources.common;

import org.apache.commons.lang.StringUtils;

public enum ResKVTypeEnum {

    movie_rating("1"), // 电影评分
    movie_summay("2"), // 电影描述
    movie_images("3"); // 电影封面

    private final String value;

    private ResKVTypeEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ResKVTypeEnum valueOfString(String value) {

        for (ResKVTypeEnum btValue : values()) {
            if (StringUtils.equalsIgnoreCase(value, btValue.getValue())) {
                return btValue;
            }
        }
        return null;
    }
}
