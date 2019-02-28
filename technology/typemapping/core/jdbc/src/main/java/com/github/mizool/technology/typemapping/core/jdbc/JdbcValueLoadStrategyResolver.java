package com.github.mizool.technology.typemapping.core.jdbc;

import java.sql.Types;
import java.util.Map;
import java.util.stream.StreamSupport;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.github.mizool.core.GuavaCollectors;
import com.github.mizool.core.exception.UnprocessableEntityException;

public class JdbcValueLoadStrategyResolver
{
    private final Map<Integer, JdbcValueLoadStrategy> strategies;

    @Inject
    protected JdbcValueLoadStrategyResolver(Instance<JdbcValueLoadStrategy> strategies)
    {
        this.strategies = StreamSupport.stream(strategies.spliterator(), false)
            .collect(GuavaCollectors.toImmutableMap(JdbcValueLoadStrategy::getSourceColumnType,
                prestoValueLoadStrategy -> prestoValueLoadStrategy));
    }

    public JdbcValueLoadStrategy resolve(int columnType)
    {
        if (columnType == Types.JAVA_OBJECT) // This is what Presto will report on a static null, i.e. "select null".
        {
            throw new UnprocessableEntityException("Untyped NULL value not supported");
        }
        if (!strategies.containsKey(columnType))
        {
            throw new UnprocessableEntityException("No strategy for column type " + columnType);
        }
        return strategies.get(columnType);
    }
}