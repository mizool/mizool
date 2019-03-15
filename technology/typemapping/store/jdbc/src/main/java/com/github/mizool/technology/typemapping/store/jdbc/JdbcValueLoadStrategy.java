package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.github.mizool.technology.typemapping.business.DataType;

public interface JdbcValueLoadStrategy
{
    List<Integer> getSourceColumnType();

    DataType getTargetDataType();

    Object loadValue(String columnName, ResultSet resultSet) throws SQLException;
}