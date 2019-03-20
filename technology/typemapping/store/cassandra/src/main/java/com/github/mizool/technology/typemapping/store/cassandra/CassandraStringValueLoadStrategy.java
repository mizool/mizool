package com.github.mizool.technology.typemapping.store.cassandra;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraStringValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    public CassandraStringValueLoadStrategy()
    {
        super(com.datastax.driver.core.DataType.text(), DataType.STRING);
    }

    @Override
    public Object loadValue(String columnName, Row row)
    {
        return row.getString(columnName);
    }
}