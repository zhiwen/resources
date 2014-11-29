package com.resources.search;

import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import com.resources.common.MovieSubTypeEnum;

public class ResMovieQueryParam extends SearchQueryParam {

    /**
     * doubanid
     */
    private Long             did;

    /**
     * 标题,包括（title, orgTitle, aka）
     */
    private String           title;

    /**
     * 类型
     */
    private Long             genreId;

    /**
     * 制作国家/地区
     */
    private Long             countryId;

    /**
     * 评分数
     */
    private OrderByEnum      ratingCount;

    /**
     * 想看人数
     */
    private OrderByEnum      wishCount;
    /**
     * 看过人数
     */
    private OrderByEnum      collectCount;
    /**
     * 条目分类, movie或者tv
     */
    private MovieSubTypeEnum subType;

    /**
     * 年代
     */
    private String           year;

    /**
     * 短评数量
     */
    private OrderByEnum      commentCount;
    /**
     * 影评数量
     */
    private OrderByEnum      reviewCount;

    /**
     * tag-id列表
     */
    private List<Long>       tagIds;

    /**
     * 是否可以播放(true/false/null)
     */
    private Boolean          playable;

    /**
     * 修改时间
     */
    private OrderByEnum      modifiedTime;

    private OrderByEnum      createdTime;

    public Long getDid() {
        return did;
    }

    public void setDid(Long did) {
        this.did = did;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public OrderByEnum getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(OrderByEnum ratingCount) {
        this.ratingCount = ratingCount;
    }

    public OrderByEnum getWishCount() {
        return wishCount;
    }

    public void setWishCount(OrderByEnum wishCount) {
        this.wishCount = wishCount;
    }

    public OrderByEnum getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(OrderByEnum collectCount) {
        this.collectCount = collectCount;
    }

    public MovieSubTypeEnum getSubType() {
        return subType;
    }

    public void setSubType(MovieSubTypeEnum subType) {
        this.subType = subType;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public OrderByEnum getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(OrderByEnum commentCount) {
        this.commentCount = commentCount;
    }

    public OrderByEnum getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(OrderByEnum reviewCount) {
        this.reviewCount = reviewCount;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public Boolean getPlayable() {
        return playable;
    }

    public void setPlayable(Boolean playable) {
        this.playable = playable;
    }

    public OrderByEnum getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(OrderByEnum modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public OrderByEnum getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(OrderByEnum createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public Query buildQuery() {

        Query queryTitle = SearchUtil.stringToQuery(ResMovieFieldNameEnum.title.getValue(), getTitle());
        if (null != queryTitle) {
            return queryTitle;
        }

        Query queryCountry = SearchUtil.longToQuery(ResMovieFieldNameEnum.countryIds.getValue(), getCountryId());
        Query queryGenreId = SearchUtil.longToQuery(ResMovieFieldNameEnum.genreIds.getValue(), getGenreId());
        Query queryYear = SearchUtil.stringToQuery(ResMovieFieldNameEnum.year.getValue(), getYear());

        BooleanQuery booleanQuery = buildBooleanQuery(queryCountry, queryGenreId, queryYear);
        if (booleanQuery != null) {
            return booleanQuery;
        }

        return null;
    }

    private BooleanQuery buildBooleanQuery(Query... queries) {
        if (queries == null) {
            return null;
        }

        BooleanQuery booleanQuery = new BooleanQuery();
        for (Query query : queries) {
            if (null == query) {
                continue;
            }
            booleanQuery.add(query, Occur.MUST);
        }
        return booleanQuery;
    }

    @Override
    public Sort buildSort() {
        List<SortField> sortFieldList = new LinkedList<SortField>();
        if (getModifiedTime() != OrderByEnum.NONE) {
            sortFieldList.add(new SortField(ResMovieFieldNameEnum.modifiedTime.getValue(),
                                            ResMovieFieldNameEnum.modifiedTime.getType(),
                                            OrderByEnum.ASC != getModifiedTime()));
        }

        if (getReviewCount() != OrderByEnum.NONE) {
            sortFieldList.add(new SortField(ResMovieFieldNameEnum.reviewCount.getValue(),
                                            ResMovieFieldNameEnum.reviewCount.getType(),
                                            OrderByEnum.ASC != getModifiedTime()));
        }

        if (getRatingCount() != OrderByEnum.NONE) {
            sortFieldList.add(new SortField(ResMovieFieldNameEnum.ratingCount.getValue(),
                                            ResMovieFieldNameEnum.ratingCount.getType(),
                                            OrderByEnum.ASC != getRatingCount()));
        }

        if (sortFieldList.isEmpty()) {
            // default
            sortFieldList.add(new SortField(ResMovieFieldNameEnum.createdTime.getValue(),
                                            ResMovieFieldNameEnum.createdTime.getType(), false));
        }

        Sort sort = new Sort(sortFieldList.toArray(new SortField[sortFieldList.size()]));
        return sort;
    }

}
