package com.github.mizool.technology.typemapping.store.cassandra;

import java.time.ZoneId;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraIntegerValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    public CassandraIntegerValueLoadStrategy()
    {
        super(com.datastax.driver.core.DataType.cint(), DataType.INTEGER);
    }

    @Override
    public Object loadValue(ZoneId zoneId, String columnName, Row row)
    {
        return row.getInt(columnName);
    }
}