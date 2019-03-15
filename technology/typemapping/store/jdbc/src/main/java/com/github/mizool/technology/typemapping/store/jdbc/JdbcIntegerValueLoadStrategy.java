package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.github.mizool.technology.typemapping.business.DataType;

class JdbcIntegerValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    public JdbcIntegerValueLoadStrategy()
    {
        super(Types.INTEGER, DataType.INTEGER);
    }

    @Override
    public Object loadValue(String columnName, ResultSet resultSet) throws SQLException
    {
        Integer result = resultSet.getInt(columnName);
        if (resultSet.wasNull())
        {
            result = null;
        }
        return result;
    }
}