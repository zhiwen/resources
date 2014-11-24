package com.resources.search;

import org.apache.lucene.search.SortField.Type;

public enum ResMovieFieldNameEnum {

    id("id", Type.LONG), //
    did("did", Type.LONG), //
    title("title", Type.STRING), //
    originalTitle("originalTitle", Type.STRING), //
    castIds("castIds", Type.LONG), //
    directorIds("directorIds", Type.LONG), //
    genreIds("genreIds", Type.LONG), //
    writerIds("writerIds", Type.LONG), //
    countryIds("countryIds", Type.LONG), //
    aka("aka", Type.STRING), //
    coverImagesId("coverImagesId", Type.LONG), //
    mobileUrl("mobileUrl", Type.STRING), //
    ratingCount("ratingCount", Type.INT), //
    ratingId("ratingId", Type.LONG), //
    wishCount("wishCount", Type.INT), //
    collectCount("collectCount", Type.INT), //
    subType("subType", Type.INT), //
    website("website", Type.STRING), //
    doubanSite("doubanSite", Type.STRING), //
    pubdates("pubdates", Type.STRING), //
    year("year", Type.STRING), //
    languages("languages", Type.STRING), //
    durations("durations", Type.STRING), //
    summaryId("summaryId", Type.LONG), //
    commentCount("commentCount", Type.INT), //
    reviewCount("reviewCount", Type.INT), //
    seasonCount("seasonCount", Type.INT), //
    seasonId("seasonId", Type.LONG), //
    episodeCount("episodeCount", Type.INT), //
    imdbId("imdbId", Type.STRING), //
    tagIds("tagIds", Type.LONG), //
    dataStatus("dataStatus", Type.INT), //
    playable("playable", Type.INT), //
    createdTime("createdTime", Type.LONG), //
    modifiedTime("modifiedTime", Type.LONG);//

    private final Type   type;
    private final String value;

    private ResMovieFieldNameEnum(String value, Type type){
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

}
