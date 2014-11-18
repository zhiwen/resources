package com.resource.spider.movie.douban;

public enum DataStatus {
    TagList(1), // douban搜索页获取id ("http://movie.douban.com/tag/")
    SubjectAbs(2), // douban一网页接口（http://movie.douban.com/j/subject_abstract?subject_id=1302814）
    SubjectApi(3), // (http://api.douban.com/v2/movie/subject/1302814)
    SubjectDetail(4);// (http://movie.douban.com/subject/3269068/)

    public final int value;

    private DataStatus(int value){
        this.value = value;
    }
}
