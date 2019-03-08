package com.github.mizool.technology.typemapping.store.cassandra;

import java.time.ZoneId;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraStringValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    public CassandraStringValueLoadStrategy()
    {
        super(com.datastax.driver.core.DataType.text(), DataType.STRING);
    }

    @Override
    public Object loadValue(ZoneId zoneId, String columnName, Row row)
    {
        return row.getString(columnName);
    }
}