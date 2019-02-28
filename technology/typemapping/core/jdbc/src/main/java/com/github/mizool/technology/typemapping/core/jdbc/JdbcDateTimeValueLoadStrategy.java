package com.github.mizool.technology.typemapping.core.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.ZoneId;

import javax.inject.Inject;

import com.github.mizool.technology.typemapping.business.DataType;

class JdbcDateTimeValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    private final JavaSqlTimestampConverter javaSqlTimestampConverter;

    @Inject
    public JdbcDateTimeValueLoadStrategy(JavaSqlTimestampConverter javaSqlTimestampConverter)
    {
        super(Types.TIMESTAMP, DataType.DATE_TIME);
        this.javaSqlTimestampConverter = javaSqlTimestampConverter;
    }

    @Override
    public Object loadValue(ZoneId zoneId, String columnName, ResultSet resultSet) throws SQLException
    {
        return javaSqlTimestampConverter.toPojo(zoneId, resultSet.getTimestamp(columnName));
    }
}