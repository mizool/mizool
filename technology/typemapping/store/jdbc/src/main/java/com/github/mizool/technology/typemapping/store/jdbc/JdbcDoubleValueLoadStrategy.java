package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.github.mizool.technology.typemapping.business.DataType;
import com.google.common.collect.ImmutableList;

class JdbcDoubleValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    public JdbcDoubleValueLoadStrategy()
    {
        super(ImmutableList.of(Types.DOUBLE), DataType.DOUBLE);
    }

    @Override
    public Object loadValue(String columnName, ResultSet resultSet) throws SQLException
    {
        Double result = resultSet.getDouble(columnName);
        if (resultSet.wasNull())
        {
            result = null;
        }
        return result;
    }
}