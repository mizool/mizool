package com.github.mizool.technology.typemapping.store.cassandra;

import lombok.Getter;

import com.github.mizool.technology.typemapping.business.DataType;

abstract class AbstractCassandraValueLoadStrategy implements CassandraValueLoadStrategy
{
    @Getter
    private final com.datastax.driver.core.DataType sourceDataType;

    @Getter
    private final DataType targetDataType;

    protected AbstractCassandraValueLoadStrategy(
        com.datastax.driver.core.DataType sourceDataType, DataType targetDataType)
    {
        this.sourceDataType = sourceDataType;
        this.targetDataType = targetDataType;
    }
}