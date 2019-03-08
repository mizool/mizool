package com.github.mizool.technology.typemapping.store.cassandra;

import java.time.ZoneId;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

public interface CassandraValueLoadStrategy
{
    com.datastax.driver.core.DataType getSourceDataType();

    DataType getTargetDataType();

    Object loadValue(ZoneId zoneId, String columnName, Row row);
}