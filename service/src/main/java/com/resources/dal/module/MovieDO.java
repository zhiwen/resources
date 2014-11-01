package com.resources.dal.module;

/***
 * 影片描述
 * 
 * @author zhiwenmizw
 */
public class MovieDO extends BaseDO {

    /**
     * 资源的唯一key
     */
    private long   resId;

    /**
     * 电影封面
     */
    private String cover;

    /**
     * 导演(JSON)
     */
    private String director;

    /**
     * 编剧(JSON)
     */
    private String screenwriter;

    /**
     * 演员(JSON)
     */
    private String performer;

    /**
     * 电影类型，动作/冒险
     */
    private long   cid;

    /**
     * 制片国家/地区: 美国
     */
    private int    country;

    /**
     * (语言: 英语)
     */
    private int    language;

    /**
     * 上映日期: 2014-10-21(中国大陆) / 2014-07-25(美国)
     */
    private long   showTime;

    /**
     * 片长: 98分钟
     */
    private int    movieLength;

    /**
     * 电影别名
     */
    private String aliasName;

    /**
     * 电影星级
     */
    private float  starLevel;

    public long getResId() {
        return resId;
    }

    public void setResId(long resId) {
        this.resId = resId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getScreenwriter() {
        return screenwriter;
    }

    public void setScreenwriter(String screenwriter) {
        this.screenwriter = screenwriter;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public int getMovieLength() {
        return movieLength;
    }

    public void setMovieLength(int movieLength) {
        this.movieLength = movieLength;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public float getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(float starLevel) {
        this.starLevel = starLevel;
    }

}
