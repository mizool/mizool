package com.github.mizool.technology.typemapping.store.cassandra;

import java.time.ZoneId;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraBooleanValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    public CassandraBooleanValueLoadStrategy()
    {
        super(com.datastax.driver.core.DataType.cboolean(), DataType.BOOLEAN);
    }

    @Override
    public Object loadValue(ZoneId zoneId, String columnName, Row row)
    {
        return row.getBool(columnName);
    }
}