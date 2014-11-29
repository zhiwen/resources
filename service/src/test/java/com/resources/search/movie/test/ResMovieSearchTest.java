package com.resources.search.movie.test;

import java.io.IOException;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.resources.search.ResMovieQueryParam;
import com.resources.search.ResMovieSeacrh;
import com.resources.search.SearchQueryParam.OrderByEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:search.xml", "classpath:com/resources/dal/mapper/test/test-service.xml" })
public class ResMovieSearchTest extends TestCase {

    @Resource
    private ResMovieSeacrh resMovieSeacrh;

    @Test
    public void testGetMovieListByParams() throws IOException {

        // resMovieSeacrh.buildMovies();

        // [52249], [14], 2006
        ResMovieQueryParam queryParam = new ResMovieQueryParam();
        queryParam.setCountryId(52249L);
        queryParam.setGenreId(14L);
        queryParam.setYear("2006");
        queryParam.setOffset(2);
        queryParam.setLimit(1000);
        queryParam.setRatingCount(OrderByEnum.DESC);

        resMovieSeacrh.getMovieListByParams(queryParam);

    }

}
