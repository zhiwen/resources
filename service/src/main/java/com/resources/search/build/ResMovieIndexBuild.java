package com.resources.search.build;

import java.lang.reflect.Field;

import com.resources.dal.dataobject.ResMovieDO;

public class ResMovieIndexBuild {

    public static void main(String[] args) {

        Field[] fields = ResMovieDO.class.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field.getName());
        }
    }
}
