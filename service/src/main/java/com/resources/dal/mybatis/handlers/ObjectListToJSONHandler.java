package com.resources.dal.mybatis.handlers;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class ObjectListToJSONHandler extends BaseTypeHandler<List<Object>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Object> parameter, JdbcType jdbcType)
                                                                                                           throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public List<Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getStringList(rs.getNString(columnName));
    }

    @Override
    public List<Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getStringList(rs.getString(columnIndex));
    }

    @Override
    public List<Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getStringList(cs.getString(columnIndex));
    }

    private List<Object> getStringList(String columnValue) {
        if (StringUtils.isBlank(columnValue)) {
            return null;
        }

        JSONArray jarray = JSON.parseArray(columnValue);
        List list = new ArrayList();
        for (Object object : jarray) {
            if (object instanceof Integer) {
                list.add(Long.valueOf(object.toString()));
                continue;
            }
            list.add(object);
        }
        return list;
    }
}
