package com.resources.dal.mybatis.handlers;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.resources.common.ResKVTypeEnum;

public class ResKVTypeEnumHandler extends BaseTypeHandler<ResKVTypeEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ResKVTypeEnum parameter, JdbcType jdbcType)
                                                                                                            throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public ResKVTypeEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getBizType(rs.getString(columnName));
    }

    @Override
    public ResKVTypeEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getBizType(rs.getString(columnIndex));
    }

    @Override
    public ResKVTypeEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getBizType(cs.getString(columnIndex));
    }

    private ResKVTypeEnum getBizType(String value) {
        return ResKVTypeEnum.valueOfString(value);
    }
}
