package com.github.mizool.technology.typemapping.store.cassandra;

import com.github.mizool.technology.typemapping.business.DataType;

class CassandraIntegerValueSaveStrategy extends AbstractCassandraValueSaveStrategy
{
    public CassandraIntegerValueSaveStrategy()
    {
        super(DataType.INTEGER, com.datastax.driver.core.DataType.cint());
    }
}