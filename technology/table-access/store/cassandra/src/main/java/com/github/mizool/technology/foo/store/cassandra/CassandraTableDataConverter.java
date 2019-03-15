package com.github.mizool.technology.foo.store.cassandra;

import java.time.ZoneId;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;

import com.github.mizool.technology.foo.business.Cell;
import com.github.mizool.technology.foo.business.Column;
import com.github.mizool.technology.foo.business.TableData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
class CassandraTableDataConverter
{
    private final CassandraColumnConverter cassandraColumnConverter;
    private final CassandraCellConverter cassandraCellConverter;

    public TableData toPojo(ZoneId zoneId, Iterable<ResultSetFuture> resultSetFutures)
    {
        TableData pojo = null;

        if (resultSetFutures != null && !Iterables.isEmpty(resultSetFutures))
        {
            TableData.TableDataBuilder builder = TableData.builder();
            Iterable<Column> columns = getColumns(resultSetFutures);
            builder.columns(columns);
            builder.rows(createStreamFromFutures(zoneId, resultSetFutures, columns));
            pojo = builder.build();
        }

        return pojo;
    }

    private Iterable<Column> getColumns(Iterable<ResultSetFuture> resultSetFutures)
    {
        ResultSet resultSet = resultSetFutures.iterator().next().getUninterruptibly();
        return StreamSupport.stream(resultSet.getColumnDefinitions().spliterator(), false)
            .map(cassandraColumnConverter::toPojo)
            .collect(ImmutableList.toImmutableList());
    }

    private Stream<Iterable<Cell>> createStreamFromFutures(
        ZoneId zoneId, Iterable<ResultSetFuture> resultSetFutures, Iterable<Column> columns)
    {
        return StreamSupport.stream(resultSetFutures.spliterator(), false)
            .map(ResultSetFuture::getUninterruptibly)
            .flatMap(rows -> createStreamFromRows(zoneId, rows, columns));
    }

    private Stream<Iterable<Cell>> createStreamFromRows(
        ZoneId zoneId, Iterable<Row> rows, Iterable<Column> columns)
    {
        return StreamSupport.stream(rows.spliterator(), false).map(row -> mapRow(zoneId, row, columns));
    }

    private Iterable<Cell> mapRow(ZoneId zoneId, Row row, Iterable<Column> columns)
    {
        return StreamSupport.stream(columns.spliterator(), false)
            .map(column -> cassandraCellConverter.toPojo(zoneId, column, row))
            .collect(ImmutableList.toImmutableList());
    }
}