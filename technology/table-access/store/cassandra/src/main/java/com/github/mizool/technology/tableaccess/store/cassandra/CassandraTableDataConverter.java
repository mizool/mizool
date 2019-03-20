package com.github.mizool.technology.tableaccess.store.cassandra;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.github.mizool.technology.tableaccess.business.Cell;
import com.github.mizool.technology.tableaccess.business.Column;
import com.github.mizool.technology.tableaccess.business.TableData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
class CassandraTableDataConverter
{
    private final CassandraColumnConverter cassandraColumnConverter;
    private final CassandraCellConverter cassandraCellConverter;

    public TableData toPojo(Iterable<ResultSetFuture> resultSetFutures)
    {
        TableData pojo = null;

        if (resultSetFutures != null && !Iterables.isEmpty(resultSetFutures))
        {
            TableData.TableDataBuilder builder = TableData.builder();
            Iterable<Column> columns = getColumns(resultSetFutures);
            builder.columns(columns);
            builder.rows(createStreamFromFutures(resultSetFutures, columns));
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
        Iterable<ResultSetFuture> resultSetFutures, Iterable<Column> columns)
    {
        return StreamSupport.stream(resultSetFutures.spliterator(), false)
            .map(ResultSetFuture::getUninterruptibly)
            .flatMap(this::toRowStream)
            .map(row -> mapRow(row, columns));
    }

    private Stream<Row> toRowStream(ResultSet resultSet)
    {
        return StreamSupport.stream(resultSet.spliterator(), false);
    }

    private Iterable<Cell> mapRow(Row row, Iterable<Column> columns)
    {
        return StreamSupport.stream(columns.spliterator(), false)
            .map(column -> cassandraCellConverter.toPojo(column, row))
            .collect(ImmutableList.toImmutableList());
    }
}