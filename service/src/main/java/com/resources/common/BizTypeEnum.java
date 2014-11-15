package com.resources.common;

/**
 * 业务类型
 * 
 * @author zhiwenmizw
 */
public enum BizTypeEnum {
    /**
     * 电影类型
     */
    MOVIE(1),
    /**
     * 图片类型
     */
    IMAGE(2),
    /**
     * 文档类型
     */
    DOC(3),
    /**
     * 帖子类型
     */
    THREAD(4),
    /**
     * 游戏
     */
    GAME(5),
    /**
     * 软件
     */
    SOFTWARE(6);

    private final int value;

    BizTypeEnum(int value){
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static BizTypeEnum valueOf(int value) {

        for (BizTypeEnum btValue : values()) {
            if (btValue.getValue() == value) {
                return btValue;
            }
        }

        return null;
    }
}
