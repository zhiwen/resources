package com.resources.common;

/**
 * 业务类型
 * 
 * @author zhiwenmizw
 */
public enum BizType {
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

    private final int type;

    BizType(int type){
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
