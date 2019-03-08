package com.github.mizool.technology.typemapping.store.cassandra;

import java.time.ZoneId;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraDoubleValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    public CassandraDoubleValueLoadStrategy()
    {
        super(com.datastax.driver.core.DataType.cdouble(), DataType.DOUBLE);
    }

    @Override
    public Object loadValue(ZoneId zoneId, String columnName, Row row)
    {
        return row.getDouble(columnName);
    }
}