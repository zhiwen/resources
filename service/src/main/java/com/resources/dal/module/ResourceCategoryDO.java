package com.resources.dal.module;


/**
 * 资源分类表
 * 
 * @author zhiwenmizw
 */
public class ResourceCategoryDO {

    private long   id;

    /**
     * 资源的唯一key
     */
    // @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    /**
     * 资源的类别(图片，视频，文库)
     */
    // @Column(name = "biz_type", nullable = false)
    private int    bizType;

    /**
     * 一级分类的id (一级分类 pid=0) 二级的则都为父分类id
     */
    // @Column(name = "pid", length = 50, unique = true, nullable = false)
    private long   pid;

    // @Column(name = "created_time", nullable = false)
    private long   createdTime;

}
