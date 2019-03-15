package com.github.mizool.technology.typemapping.store.cassandra;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraDoubleValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    public CassandraDoubleValueLoadStrategy()
    {
        super(com.datastax.driver.core.DataType.cdouble(), DataType.DOUBLE);
    }

    @Override
    public Object loadValue(String columnName, Row row)
    {
        return row.getDouble(columnName);
    }
}