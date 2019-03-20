package com.github.mizool.technology.typemapping.store.cassandra;

import java.time.Instant;

import javax.inject.Inject;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraInstantValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    @Inject
    public CassandraInstantValueLoadStrategy()
    {
        super(com.datastax.driver.core.DataType.timestamp(), DataType.INSTANT);
    }

    @Override
    public Object loadValue(String columnName, Row row)
    {
        return row.get(columnName, Instant.class);
    }
}