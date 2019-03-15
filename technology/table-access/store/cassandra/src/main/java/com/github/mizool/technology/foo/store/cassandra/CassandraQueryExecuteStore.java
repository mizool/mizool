package com.github.mizool.technology.foo.store.cassandra;

import java.time.ZoneId;
import java.util.stream.Stream;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.github.mizool.core.GuavaCollectors;
import com.github.mizool.technology.foo.business.TableData;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class CassandraQueryExecuteStore
{
    private final CassandraTableDataConverter converter;

    public TableData execute(ZoneId zoneId, Stream<Statement> statements, Session cassandraSession)
    {
        Iterable<ResultSetFuture> resultSetFutures = statements.map(cassandraSession::executeAsync)
            .collect(GuavaCollectors.toImmutableList());
        return converter.toPojo(zoneId, resultSetFutures);
    }
}