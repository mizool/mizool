package com.github.mizool.technology.typemapping.core.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.ZoneId;

import com.github.mizool.technology.typemapping.business.DataType;

class JdbcStringFromVarcharValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    public JdbcStringFromVarcharValueLoadStrategy()
    {
        super(Types.VARCHAR, DataType.STRING);
    }

    @Override
    public Object loadValue(ZoneId zoneId, String columnName, ResultSet resultSet) throws SQLException
    {
        return resultSet.getString(columnName);
    }
}