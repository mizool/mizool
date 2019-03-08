package com.github.mizool.technology.typemapping.store.cassandra;

import java.util.Map;
import java.util.stream.StreamSupport;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.github.mizool.core.exception.StoreLayerException;
import com.google.common.collect.ImmutableMap;

public class CassandraValueLoadStrategyResolver
{
    private final Map<com.datastax.driver.core.DataType, CassandraValueLoadStrategy> strategies;

    @Inject
    protected CassandraValueLoadStrategyResolver(Instance<CassandraValueLoadStrategy> strategies)
    {
        this.strategies = StreamSupport.stream(strategies.spliterator(), false)
            .collect(ImmutableMap.toImmutableMap(CassandraValueLoadStrategy::getSourceDataType,
                cassandraValueLoadStrategy -> cassandraValueLoadStrategy));
    }

    public CassandraValueLoadStrategy resolve(com.datastax.driver.core.DataType dataType)
    {
        if (!strategies.containsKey(dataType))
        {
            throw new StoreLayerException("No strategy for data type " + dataType);
        }
        return strategies.get(dataType);
    }
}