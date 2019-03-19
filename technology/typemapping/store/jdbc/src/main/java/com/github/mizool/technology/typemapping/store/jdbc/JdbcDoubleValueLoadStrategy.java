package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.github.mizool.technology.typemapping.business.DataType;

class JdbcDoubleValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    public JdbcDoubleValueLoadStrategy()
    {
        super(Types.DOUBLE, DataType.DOUBLE);
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