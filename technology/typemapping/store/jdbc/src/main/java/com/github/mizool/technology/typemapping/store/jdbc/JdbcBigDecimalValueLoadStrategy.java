package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.github.mizool.technology.typemapping.business.DataType;
import com.google.common.collect.ImmutableList;

class JdbcBigDecimalValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    public JdbcBigDecimalValueLoadStrategy()
    {
        super(ImmutableList.of(Types.DECIMAL), DataType.BIG_DECIMAL);
    }

    @Override
    public Object loadValue(String columnName, ResultSet resultSet) throws SQLException
    {
        return resultSet.getBigDecimal(columnName);
    }
}