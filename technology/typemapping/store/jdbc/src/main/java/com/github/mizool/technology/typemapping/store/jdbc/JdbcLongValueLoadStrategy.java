package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.github.mizool.technology.typemapping.business.DataType;
import com.google.common.collect.ImmutableList;

class JdbcLongValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    public JdbcLongValueLoadStrategy()
    {
        super(ImmutableList.of(Types.BIGINT), DataType.LONG);
    }

    @Override
    public Object loadValue(String columnName, ResultSet resultSet) throws SQLException
    {
        Long result = resultSet.getLong(columnName);
        if (resultSet.wasNull())
        {
            result = null;
        }
        return result;
    }
}