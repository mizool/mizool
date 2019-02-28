package com.github.mizool.technology.typemapping.core.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.ZoneId;

import com.github.mizool.technology.typemapping.business.DataType;

class JdbcBigDecimalValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    public JdbcBigDecimalValueLoadStrategy()
    {
        super(Types.DECIMAL, DataType.BIG_DECIMAL);
    }

    @Override
    public Object loadValue(ZoneId zoneId, String columnName, ResultSet resultSet) throws SQLException
    {
        return resultSet.getBigDecimal(columnName);
    }
}