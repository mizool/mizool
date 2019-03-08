package com.github.mizool.technology.typemapping.store.cassandra;

import java.time.ZoneId;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraBigDecimalValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    public CassandraBigDecimalValueLoadStrategy()
    {
        super(com.datastax.driver.core.DataType.decimal(), DataType.BIG_DECIMAL);
    }

    @Override
    public Object loadValue(ZoneId zoneId, String columnName, Row row)
    {
        return row.getDecimal(columnName);
    }
}