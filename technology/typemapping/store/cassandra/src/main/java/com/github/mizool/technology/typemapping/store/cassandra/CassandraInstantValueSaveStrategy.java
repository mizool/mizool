package com.github.mizool.technology.typemapping.store.cassandra;

import com.github.mizool.technology.typemapping.business.DataType;

class CassandraInstantValueSaveStrategy extends AbstractCassandraValueSaveStrategy
{
    public CassandraInstantValueSaveStrategy()
    {
        super(DataType.INSTANT, com.datastax.driver.core.DataType.timestamp());
    }
}