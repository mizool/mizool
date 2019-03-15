package com.github.mizool.technology.typemapping.store.jdbc;

import java.util.List;

import lombok.Getter;

import com.github.mizool.technology.typemapping.business.DataType;

abstract class AbstractJdbcValueLoadStrategy implements JdbcValueLoadStrategy
{
    @Getter
    private final List<Integer> sourceColumnType;

    @Getter
    private final DataType targetDataType;

    protected AbstractJdbcValueLoadStrategy(List<Integer> sourceColumnType, DataType targetDataType)
    {
        this.sourceColumnType = sourceColumnType;
        this.targetDataType = targetDataType;
    }
}