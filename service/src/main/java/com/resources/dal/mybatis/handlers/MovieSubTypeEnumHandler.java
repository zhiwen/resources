package com.resources.dal.mybatis.handlers;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.resources.common.MovieSubTypeEnum;

public class MovieSubTypeEnumHandler extends BaseTypeHandler<MovieSubTypeEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MovieSubTypeEnum parameter, JdbcType jdbcType)
                                                                                                           throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public MovieSubTypeEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getMovieSubType(rs.getInt(columnName));
    }

    @Override
    public MovieSubTypeEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getMovieSubType(rs.getInt(columnIndex));
    }

    @Override
    public MovieSubTypeEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getMovieSubType(cs.getInt(columnIndex));
    }

    private MovieSubTypeEnum getMovieSubType(int value) {
        return MovieSubTypeEnum.valueOf(value);
    }
}
