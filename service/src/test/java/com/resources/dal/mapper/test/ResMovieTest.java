package com.resources.dal.mapper.test;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.resources.common.MovieSubTypeEnum;
import com.resources.dal.dataobject.ResMovieDO;
import com.resources.service.ResMovieService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:com/resources/dal/mapper/test/test-service.xml" })
public class ResMovieTest extends TestCase {

    @Resource
    private ResMovieService resMovieService;

    public ResMovieDO get() {
        ResMovieDO movie = new ResMovieDO();

        movie.setDid(25845393);
        movie.setTitle("生活大爆炸 第八季");
        movie.setOriginalTitle("The Big Bang Theory Season 8");

        ImmutableList<Long> castIds = ImmutableList.of(12L);
        movie.setCastIds(castIds);

        ImmutableList<Long> directorIds = ImmutableList.of(13L);
        movie.setDirectorIds(directorIds);

        ImmutableList<Long> writerIds = ImmutableList.of(14L);
        movie.setWriterIds(writerIds);

        ImmutableList<Long> countryIds = ImmutableList.of(15L);
        movie.setCountryIds(countryIds);

        ImmutableList<String> aka = ImmutableList.of("天才也性感 第八季", "天才理论传 第八季", "大爆炸理论 第八季", "宅男行不行 第八季(台)");
        movie.setAka(aka);

        movie.setMobileUrl("http://movie.douban.com/subject/25845393/mobile");

        movie.setRatingCount(4497);
        movie.setRatingId(1);
        movie.setWishCount(3322);
        movie.setCollectCount(2151);
        movie.setSubType(MovieSubTypeEnum.tv);
        movie.setWebsite("www.cbs.com/shows/big_bang_theory/");
        movie.setDoubanSite(null);
        movie.setPubdates("2014-09-22");
        movie.setYear("2014");

        ImmutableList<String> languages = ImmutableList.of("英语");
        movie.setLanguages(languages);

        movie.setDurations("20");
        movie.setSummaryId(1);
        movie.setCommentCount(498);
        movie.setReviewCount(11);
        movie.setSeasonCount(10);
        movie.setEpisodeCount(24);
        movie.setImdbId("t3596130");

        ImmutableList<Long> tagIds = ImmutableList.of(1L, 2L, 3L);
        movie.setTagIds(tagIds);

        movie.setCreatedTime(new Date());
        movie.setModifiedTime(movie.getCreatedTime());
        System.out.println(JSON.toJSON(movie));
        return movie;
    }

    @Test
    public void testAddResMovie() {
        ResMovieDO movie = get();
        resMovieService.addMovie(movie);
        Assert.assertTrue(movie.getId() > 0);
    }

    @Test
    public void testUpdateMovie() {
        ResMovieDO movie = get();
        resMovieService.addMovie(movie);
        movie.setTitle(movie.getTitle() + "updated");
        int r = resMovieService.updateMovie(movie);
        Assert.assertTrue(r > 0);
    }

    @Test
    public void testGetResMovie() {
        ResMovieDO movie = resMovieService.getMovie(1L);
        Assert.assertTrue(movie != null);
        Assert.assertTrue(!movie.getAka().isEmpty());
        Assert.assertTrue(movie.getSubType().getValue() == MovieSubTypeEnum.tv.getValue());
    }

    @Test
    public void testGetMovieByPaginatorWithStatus() {
        List<ResMovieDO> movieList = resMovieService.getMovieByPaginatorWithStatus(0, 0, 100);
        Assert.assertTrue(movieList != null);
        Assert.assertTrue(!movieList.isEmpty());
    }

}
