package com.resources.dal.module;

/**
 * 资源tag表, 一个资源有多个tag
 * 
 * @author zhiwenmizw
 */
// @Table(name = "resource_tags")
public class ResourceTagsDO {

    /**
     * 资源的唯一key
     */
    // @Column(name = "resource_key", length = 50, nullable = false)
    private String resourceKey;

    /**
     * 资源类型(电影、资料、图片)
     */
    // @Column(name = "biz_type")
    private int    bizType;

    /**
     * 标签次数
     */
    // @Column(name = "counts")
    private long   counts;

    /**
     * 所属类目id
     */
    // @Column(name = "cid")
    private long   cid;

    // @Column(name = "modified_time", nullable = false)
    private long   modifiedTime;
}
