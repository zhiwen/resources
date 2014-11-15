package com.resources.dal.dataobject;

import java.util.Date;
import java.util.List;

import com.resources.common.BaseDO;
import com.resources.common.MovieSubTypeEnum;

public class ResMovieDO extends BaseDO {

    private long             id;

    /**
     * doubanid
     */
    private long             did;

    /**
     * 标题
     */
    private String           title;
    /**
     * 原标题
     */
    private String           originalTitle;

    /**
     * 演员
     */
    private List<Long>       castIds;

    /**
     * 导演
     */
    private List<Long>       directorIds;

    /**
     * 作家
     */
    private List<Long>       writerIds;
    /**
     * 制作国家/地区
     */
    private List<Long>       countryIds;
    /**
     * 又名 ["天才也性感 第八季","天才理论传 第八季","大爆炸理论 第八季","宅男行不行 第八季(台)"]
     */
    private List<String>     aka;
    /**
     * 地址
     */
    private String           alt;
    /**
     * 移动版url
     */
    private String           mobileUrl;
    /**
     * 评分数
     */
    private int              ratingCount;
    /**
     * 评分详细信息id
     */
    private long             ratingId;
    /**
     * 想看人数
     */
    private int              wishCount;
    /**
     * 看过人数
     */
    private int              collectCount;
    /**
     * 条目分类, movie或者tv
     */
    private MovieSubTypeEnum subType;
    /**
     * 官方网站
     */
    private String           website;
    /**
     * 豆瓣小站
     */
    private String           doubanSite;
    /**
     * 如果条目类型是电影则为上映日期，如果是电视剧则为首Ï日期
     */
    private String           pubdates;
    /**
     * 大陆上映日期，如果条目类型是电影则为上映日期，如果是电视剧则为首播日期
     */
    private String           mainlandPubdate;
    /**
     * 年代
     */
    private String           year;
    /**
     * 语言
     */
    private List<String>     languages;
    /**
     * 片长
     */
    private String           durations;
    /**
     * 摘要详情id
     */
    private long             summaryId;
    /**
     * 短评数量
     */
    private int              commentCount;
    /**
     * 影评数量
     */
    private int              reviewCount;
    /**
     * 总季数(tv only)
     */
    private int              seasonCount;
    /**
     * 当前季数(tv only)
     */
    private int              currentSeason;
    /**
     * 当前季的集数(tv only)
     */
    private int              episodeCount;
    /**
     * imdbId
     */
    private String           imdbId;
    /**
     * tag-id列表
     */
    private List<Long>       tagIds;
    /**
     * 修改时间
     */
    private Date             modifiedTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDid() {
        return did;
    }

    public void setDid(long did) {
        this.did = did;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public List<Long> getCastIds() {
        return castIds;
    }

    public void setCastIds(List<Long> castIds) {
        this.castIds = castIds;
    }

    public List<Long> getDirectorIds() {
        return directorIds;
    }

    public void setDirectorIds(List<Long> directorIds) {
        this.directorIds = directorIds;
    }

    public List<Long> getWriterIds() {
        return writerIds;
    }

    public void setWriterIds(List<Long> writerIds) {
        this.writerIds = writerIds;
    }

    public List<Long> getCountryIds() {
        return countryIds;
    }

    public void setCountryIds(List<Long> countryIds) {
        this.countryIds = countryIds;
    }

    public List<String> getAka() {
        return aka;
    }

    public void setAka(List<String> aka) {
        this.aka = aka;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public long getRatingId() {
        return ratingId;
    }

    public void setRatingId(long ratingId) {
        this.ratingId = ratingId;
    }

    public int getWishCount() {
        return wishCount;
    }

    public void setWishCount(int wishCount) {
        this.wishCount = wishCount;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public MovieSubTypeEnum getSubType() {
        return subType;
    }

    public void setSubType(MovieSubTypeEnum subType) {
        this.subType = subType;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDoubanSite() {
        return doubanSite;
    }

    public void setDoubanSite(String doubanSite) {
        this.doubanSite = doubanSite;
    }

    public String getPubdates() {
        return pubdates;
    }

    public void setPubdates(String pubdates) {
        this.pubdates = pubdates;
    }

    public String getMainlandPubdate() {
        return mainlandPubdate;
    }

    public void setMainlandPubdate(String mainlandPubdate) {
        this.mainlandPubdate = mainlandPubdate;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getDurations() {
        return durations;
    }

    public void setDurations(String durations) {
        this.durations = durations;
    }

    public long getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(long summaryId) {
        this.summaryId = summaryId;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getSeasonCount() {
        return seasonCount;
    }

    public void setSeasonCount(int seasonCount) {
        this.seasonCount = seasonCount;
    }

    public int getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(int currentSeason) {
        this.currentSeason = currentSeason;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
