package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.inject.Inject;

import com.github.mizool.technology.typemapping.business.DataType;

class JdbcInstantValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    private final JavaSqlTimestampConverter javaSqlTimestampConverter;

    @Inject
    public JdbcInstantValueLoadStrategy(JavaSqlTimestampConverter javaSqlTimestampConverter)
    {
        super(Types.TIMESTAMP, DataType.INSTANT);
        this.javaSqlTimestampConverter = javaSqlTimestampConverter;
    }

    @Override
    public Object loadValue(String columnName, ResultSet resultSet) throws SQLException
    {
        return javaSqlTimestampConverter.toPojo(resultSet.getTimestamp(columnName));
    }
}