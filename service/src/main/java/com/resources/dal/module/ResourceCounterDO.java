package com.resources.dal.module;

/**
 * 资源计数器
 * 
 * @author zhiwenmizw
 */
// @Table(name = "resource_counter", uniqueConstraints = { @UniqueConstraint(name = "uq$rk:bt:at", columnNames = {
// "resource_key",
// "biz_type",
// "action_type" }) })
public class ResourceCounterDO {

    /**
     * 资源的唯一key
     */
    // @Column(name = "resource_key", length = 50, nullable = false)
    private String resourceKey;

    /**
     * 事件类型(评论，喜欢，浏览，下载，打标数...)
     */
    // @Column(name = "action_type", nullable = false)
    private int    actionType;

    /**
     * 资源类型(电影、资料、图片)
     */
    // @Column(name = "biz_type")
    private int    bizType;

    /**
     * 计数
     */
    // @Column(name = "action_counts")
    private long   actionCounts;

    /**
     * 所属类目id
     */
    // @Column(name = "cid")
    private long   cid;

    // @Column(name = "created_time", nullable = false)
    private long   createdTime;
}
