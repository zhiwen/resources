package com.resources.dal.dataobject;

import java.util.Date;
import java.util.List;

public class ResMovie extends BaseDO {

    private long         id;

    /**
     * doubanid
     */
    private long         did;

    /**
     * 标题
     */
    private String       title;
    /**
     * 原标题
     */
    private String       originalTitle;

    /**
     * 演员
     */
    private List<Long>   castIds;

    /**
     * 导演
     */
    private List<Long>   directorIds;

    /**
     * 作家
     */
    private List<Long>   writerIds;
    /**
     * 制作国家/地区
     */
    private List<Long>   countryIds;
    /**
     * 又名
     */
    private List<String> aka;
    /**
     * 地址
     */
    private String       alt;
    /**
     * 移动版url
     */
    private String       mobileUrl;
    /**
     * 评分数
     */
    private int          ratingCount;
    /**
     * 评分详细信息id
     */
    private long         ratingId;
    /**
     * 想看人数
     */
    private int          wishCount;
    /**
     * 看过人数
     */
    private int          collectCount;
    /**
     * 条目分类, movie或者tv
     */
    private int          subType;
    /**
     * 官方网站
     */
    private String       websiteUrl;
    /**
     * 豆瓣小站
     */
    private String       doubanSite;
    /**
     * 如果条目类型是电影则为上映日期，如果是电视剧则为首Ï日期
     */
    private String       pubDates;
    /**
     * 大陆上映日期，如果条目类型是电影则为上映日期，如果是电视剧则为首播日期
     */
    private String       mainlandPubdate;
    /**
     * 年代
     */
    private String       year;
    /**
     * 语言
     */
    private String       languages;
    /**
     * 片长
     */
    private String       durations;
    /**
     * 摘要详情id
     */
    private long         summaryId;
    /**
     * 短评数量
     */
    private int          commentCount;
    /**
     * 影评数量
     */
    private int          reviewCount;
    /**
     * 总季数(tv only)
     */
    private int          seasonCount;
    /**
     * 当前季数(tv only)
     */
    private int          currentSeason;
    /**
     * 当前季的集数(tv only)
     */
    private int          episodeCount;
    /**
     * imdbId
     */
    private String       imdbId;
    /**
     * tag-id列表
     */
    private List<String> tagIds;
    /**
     * 修改时间
     */
    private Date         modifiedTime;
}
