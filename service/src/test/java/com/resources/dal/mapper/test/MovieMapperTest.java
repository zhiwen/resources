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

import com.resources.dal.module.MovieDO;
import com.resources.service.MovieService;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:com/resources/dal/mapper/test/test-service.xml" })
public class MovieMapperTest extends TestCase {

    @Resource
    private MovieService movieService;

    private MovieDO getMovieDO() {
        MovieDO movieDO = new MovieDO();

        movieDO.setResId(1);
        movieDO.setCid(1);
        movieDO.setCountry(1);
        movieDO.setCover("http://img3.douban.com/view/site/large/public/f92ffd2d4a123ee.jpg");
        movieDO.setAliasName("大空一号");
        movieDO.setDirector("xiamigg");
        movieDO.setPerformer("007");
        movieDO.setScreenwriter("boundle");
        movieDO.setLanguage(1);
        movieDO.setShowTime(360);
        movieDO.setMovieLength(1024);
        movieDO.setStarLevel(8.4f);
        movieDO.setCreatedTime(new Date());
        movieDO.setModifiedTime(movieDO.getCreatedTime());
        return movieDO;
    }

    @Test
    public void testAddMovie() {
        long id = movieService.addMovie(getMovieDO());
        Assert.assertTrue(id > 0);
    }

    @Test
    public void testDeleteMovie() {
        MovieDO movie = getMovieDO();
        movie.setResId(2);
        movieService.addMovie(movie);
        int r = movieService.deleteMovie(movie.getResId());
        Assert.assertTrue(r > 0);
    }

    @Test
    public void testUpdateMovie() {
        MovieDO movieDO = getMovieDO();
        movieDO.setResId(1);
        movieDO.setCid(2);
        movieDO.setStarLevel(9.0f);
        movieDO.setModifiedTime(new Date());
        int r = movieService.updateMovie(movieDO);
        Assert.assertTrue(r > 0);
    }

    @Test
    public void testGetMovie() {
        MovieDO movieDO = movieService.getMovie(1);
        Assert.assertTrue(movieDO != null);
    }

    @Test
    public void testGetMovieOrderByCreatedTest() {
        List<MovieDO> list = movieService.getMovieOrderByCreated(2, 2, 2, 0, 10);
        Assert.assertTrue(list.size() > 1);
    }

}
