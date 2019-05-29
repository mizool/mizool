package com.github.mizool.technology.typemapping.store.cassandra;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.github.mizool.technology.typemapping.business.DataType;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractCassandraValueLoadStrategy implements CassandraValueLoadStrategy
{
    @Getter
    private final com.datastax.driver.core.DataType sourceDataType;

    @Getter
    private final DataType targetDataType;
}