package com.github.mizool.technology.typemapping.store.cassandra;

import com.github.mizool.technology.typemapping.business.DataType;

class CassandraStringValueSaveStrategy extends AbstractCassandraValueSaveStrategy
{
    public CassandraStringValueSaveStrategy()
    {
        super(DataType.STRING, com.datastax.driver.core.DataType.text());
    }
}