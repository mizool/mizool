package com.github.mizool.technology.typemapping.store.jdbc;

import lombok.Getter;

import com.github.mizool.technology.typemapping.business.DataType;

abstract class AbstractJdbcValueLoadStrategy implements JdbcValueLoadStrategy
{
    @Getter
    private final int sourceColumnType;

    @Getter
    private final DataType targetDataType;

    protected AbstractJdbcValueLoadStrategy(int sourceColumnType, DataType targetDataType)
    {
        this.sourceColumnType = sourceColumnType;
        this.targetDataType = targetDataType;
    }
}