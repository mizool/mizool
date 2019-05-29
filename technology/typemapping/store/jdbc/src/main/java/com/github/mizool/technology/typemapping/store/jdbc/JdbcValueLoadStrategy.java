package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import com.github.mizool.technology.typemapping.business.DataType;

public interface JdbcValueLoadStrategy
{
    Set<Integer> getSourceColumnTypes();

    DataType getTargetDataType();

    Object loadValue(String columnName, ResultSet resultSet) throws SQLException;
}