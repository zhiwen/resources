package com.resources.search.movie.test;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.resources.search.ResMovieFieldNameEnum;
import com.resources.search.ResMovieSeacrh;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:search.xml", "classpath:com/resources/dal/mapper/test/test-service.xml" })
public class ResMovieSearchTest extends TestCase {

    @Resource
    private ResMovieSeacrh resMovieSeacrh;

    @Test
    public void testGetMovieListByParams() {

        // try {
        // resMovieSeacrh.buildMovies();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        // [52249], [14], 2006
        resMovieSeacrh.getMovieListByParams("52249", "14", "2006", ResMovieFieldNameEnum.createdTime, 0, 50);

    }

}
