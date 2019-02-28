package com.github.mizool.technology.typemapping.core.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.ZoneId;

import com.github.mizool.technology.typemapping.business.DataType;

class JdbcLongValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    public JdbcLongValueLoadStrategy()
    {
        super(Types.BIGINT, DataType.LONG);
    }

    @Override
    public Object loadValue(ZoneId zoneId, String columnName, ResultSet resultSet) throws SQLException
    {
        Long result = resultSet.getLong(columnName);
        if (resultSet.wasNull())
        {
            result = null;
        }
        return result;
    }
}