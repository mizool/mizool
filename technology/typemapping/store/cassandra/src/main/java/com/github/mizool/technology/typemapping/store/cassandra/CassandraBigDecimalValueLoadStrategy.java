package com.github.mizool.technology.typemapping.store.cassandra;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraBigDecimalValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    public CassandraBigDecimalValueLoadStrategy()
    {
        super(com.datastax.driver.core.DataType.decimal(), DataType.BIG_DECIMAL);
    }

    @Override
    public Object loadValue(String columnName, Row row)
    {
        return row.getDecimal(columnName);
    }
}