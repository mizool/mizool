package com.github.mizool.technology.typemapping.store.cassandra;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

public interface CassandraValueLoadStrategy
{
    com.datastax.driver.core.DataType getSourceDataType();

    DataType getTargetDataType();

    Object loadValue(String columnName, Row row);
}