package com.github.mizool.technology.typemapping.store.cassandra;

import java.util.Map;
import java.util.stream.StreamSupport;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.github.mizool.core.GuavaCollectors;
import com.github.mizool.core.exception.StoreLayerException;
import com.github.mizool.technology.typemapping.business.DataType;

public class CassandraValueSaveStrategyResolver
{
    private final Map<DataType, CassandraValueSaveStrategy> strategies;

    @Inject
    protected CassandraValueSaveStrategyResolver(Instance<CassandraValueSaveStrategy> strategies)
    {
        this.strategies = StreamSupport.stream(strategies.spliterator(), false)
            .collect(GuavaCollectors.toImmutableMap(CassandraValueSaveStrategy::getSourceDataType,
                cassandraValueSaveStrategy -> cassandraValueSaveStrategy));
    }

    public CassandraValueSaveStrategy resolve(DataType dataType)
    {
        if (!strategies.containsKey(dataType))
        {
            throw new StoreLayerException("No strategy for data type " + dataType);
        }
        return strategies.get(dataType);
    }
}