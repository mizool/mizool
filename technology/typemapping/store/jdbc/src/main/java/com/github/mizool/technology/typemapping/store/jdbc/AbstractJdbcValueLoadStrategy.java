package com.github.mizool.technology.typemapping.store.jdbc;

import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.github.mizool.technology.typemapping.business.DataType;
import com.google.common.collect.ImmutableSet;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractJdbcValueLoadStrategy implements JdbcValueLoadStrategy
{
    @Getter
    private final Set<Integer> sourceColumnTypes;

    @Getter
    private final DataType targetDataType;

    protected AbstractJdbcValueLoadStrategy(Integer sourceColumnType, DataType targetDataType)
    {
        this(ImmutableSet.of(sourceColumnType), targetDataType);
    }
}