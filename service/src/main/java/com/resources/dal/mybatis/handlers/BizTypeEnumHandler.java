package com.resources.dal.mybatis.handlers;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.resources.common.BizTypeEnum;

public class BizTypeEnumHandler extends BaseTypeHandler<BizTypeEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BizTypeEnum parameter, JdbcType jdbcType)
                                                                                                      throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public BizTypeEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getBizType(rs.getInt(columnName));
    }

    @Override
    public BizTypeEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getBizType(rs.getInt(columnIndex));
    }

    @Override
    public BizTypeEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getBizType(cs.getInt(columnIndex));
    }

    private BizTypeEnum getBizType(int value) {
        return BizTypeEnum.valueOf(value);
    }
}
