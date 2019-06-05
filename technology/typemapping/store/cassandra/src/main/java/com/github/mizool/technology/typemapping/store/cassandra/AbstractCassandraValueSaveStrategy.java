package com.github.mizool.technology.typemapping.store.cassandra;

import lombok.Getter;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.github.mizool.technology.tableaccess.business.Cell;
import com.github.mizool.technology.typemapping.business.DataType;

abstract class AbstractCassandraValueSaveStrategy implements CassandraValueSaveStrategy
{
    @Getter
    private final DataType sourceDataType;

    @Getter
    private final com.datastax.driver.core.DataType targetDataType;

    protected AbstractCassandraValueSaveStrategy(
        DataType sourceDataType, com.datastax.driver.core.DataType targetDataType)
    {
        this.sourceDataType = sourceDataType;
        this.targetDataType = targetDataType;
    }

    @Override
    public void saveValue(Cell cell, Insert insert)
    {
        Object value = cell.getValue();
        if (value != null)
        {
            insert.value(QueryBuilder.quote(cell.getColumn().getName()), convertValue(value));
        }
    }

    protected Object convertValue(Object value)
    {
        return value;
    }
}