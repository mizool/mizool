package com.github.mizool.technology.typemapping.store.cassandra;

import com.github.mizool.technology.typemapping.business.DataType;

class CassandraBooleanValueSaveStrategy extends AbstractCassandraValueSaveStrategy
{
    public CassandraBooleanValueSaveStrategy()
    {
        super(DataType.BOOLEAN, com.datastax.driver.core.DataType.cboolean());
    }
}