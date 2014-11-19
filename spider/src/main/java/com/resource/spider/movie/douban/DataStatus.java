package com.resource.spider.movie.douban;

public enum DataStatus {
    doubanMovieId(1), // 获取所有的电影id
    doubanMovieAbstract(2), // douban一网页接口（http://movie.douban.com/j/subject_abstract?subject_id=1302814）
    doubanMovieApi(3), // (http://api.douban.com/v2/movie/subject/1302814)
    doubanMovieDetail(4);// (http://movie.douban.com/subject/3269068/)

    private final int value;

    private DataStatus(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
