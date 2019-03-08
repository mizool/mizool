package com.github.mizool.technology.typemapping.store.cassandra;

import com.github.mizool.technology.typemapping.business.DataType;

class CassandraBigDecimalValueSaveStrategy extends AbstractCassandraValueSaveStrategy
{
    public CassandraBigDecimalValueSaveStrategy()
    {
        super(DataType.BIG_DECIMAL, com.datastax.driver.core.DataType.decimal());
    }
}