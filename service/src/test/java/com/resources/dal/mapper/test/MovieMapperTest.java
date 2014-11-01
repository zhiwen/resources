package com.resources.dal.mapper.test;

import junit.framework.TestCase;

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

    private MovieService movieService;

    public void testAddMovie() {

        MovieDO movieDO = new MovieDO();

        movieDO.setResId(1);

        movieService.addMovie(movieDO);
    }

    public void testDeleteMovie() {

    }

    public void testUpdateMovie() {

    }

    public void testGetMovie() {

    }
}
