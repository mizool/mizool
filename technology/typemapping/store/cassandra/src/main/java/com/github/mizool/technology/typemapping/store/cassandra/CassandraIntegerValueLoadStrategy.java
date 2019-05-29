package com.github.mizool.technology.typemapping.store.cassandra;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraIntegerValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    public CassandraIntegerValueLoadStrategy()
    {
        super(com.datastax.driver.core.DataType.cint(), DataType.INTEGER);
    }

    @Override
    public Object loadValue(String columnName, Row row)
    {
        return row.getInt(columnName);
    }
}