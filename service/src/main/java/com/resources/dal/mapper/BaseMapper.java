package com.resources.dal.mapper;

public interface BaseMapper<DO, PK> {

    public int addData(DO value);

    public int delData(PK pkId);

    public int updateData(DO value);

    public DO getData(PK pkId);
}
