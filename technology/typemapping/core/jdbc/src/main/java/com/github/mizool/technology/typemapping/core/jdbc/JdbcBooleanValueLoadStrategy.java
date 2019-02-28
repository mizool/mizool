package com.github.mizool.technology.typemapping.core.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.ZoneId;

import com.github.mizool.technology.typemapping.business.DataType;

class JdbcBooleanValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    public JdbcBooleanValueLoadStrategy()
    {
        super(Types.BOOLEAN, DataType.BOOLEAN);
    }

    @Override
    public Object loadValue(ZoneId zoneId, String columnName, ResultSet resultSet) throws SQLException
    {
        Boolean result = resultSet.getBoolean(columnName);
        if (resultSet.wasNull())
        {
            result = null;
        }
        return result;
    }
}