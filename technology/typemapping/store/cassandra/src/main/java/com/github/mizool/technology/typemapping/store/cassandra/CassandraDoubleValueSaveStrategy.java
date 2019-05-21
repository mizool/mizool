package com.github.mizool.technology.typemapping.store.cassandra;

import com.github.mizool.technology.typemapping.business.DataType;

class CassandraDoubleValueSaveStrategy extends AbstractCassandraValueSaveStrategy
{
    public CassandraDoubleValueSaveStrategy()
    {
        super(DataType.DOUBLE, com.datastax.driver.core.DataType.cdouble());
    }
}