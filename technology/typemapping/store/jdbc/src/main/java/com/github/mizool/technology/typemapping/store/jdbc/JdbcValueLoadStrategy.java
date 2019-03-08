package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

import com.github.mizool.technology.typemapping.business.DataType;

public interface JdbcValueLoadStrategy
{
    int getSourceColumnType();

    DataType getTargetDataType();

    Object loadValue(ZoneId zoneId, String columnName, ResultSet resultSet) throws SQLException;
}