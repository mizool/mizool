package com.github.mizool.technology.typemapping.store.cassandra;

import com.datastax.driver.core.querybuilder.Insert;
import com.github.mizool.technology.foo.business.Cell;
import com.github.mizool.technology.typemapping.business.DataType;

public interface CassandraValueSaveStrategy
{
    DataType getSourceDataType();

    com.datastax.driver.core.DataType getTargetDataType();

    void saveValue(Cell cell, Insert insert);
}