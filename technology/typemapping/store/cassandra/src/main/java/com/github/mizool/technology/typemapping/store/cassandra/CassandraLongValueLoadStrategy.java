package com.github.mizool.technology.typemapping.store.cassandra;

import java.time.ZoneId;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraLongValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    public CassandraLongValueLoadStrategy()
    {
        super(com.datastax.driver.core.DataType.bigint(), DataType.LONG);
    }

    @Override
    public Object loadValue(ZoneId zoneId, String columnName, Row row)
    {
        return row.getLong(columnName);
    }
}