package com.github.mizool.technology.typemapping.store.cassandra;

import com.github.mizool.technology.typemapping.business.DataType;

class CassandraLongValueSaveStrategy extends AbstractCassandraValueSaveStrategy
{
    public CassandraLongValueSaveStrategy()
    {
        super(DataType.LONG, com.datastax.driver.core.DataType.bigint());
    }
}