package com.github.mizool.technology.tableaccess.store.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.exception.StoreLayerException;
import com.github.mizool.technology.tableaccess.business.Cell;
import com.github.mizool.technology.tableaccess.business.Column;
import com.github.mizool.technology.tableaccess.business.TableData;
import com.google.common.collect.ImmutableList;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class JdbcTableDataConverter
{
    private final JdbcColumnConverter jdbcColumnConverter;
    private final JdbcCellConverter jdbcCellConverter;

    public TableData toPojo(ResultSet resultSet, Statement statement, Connection connection)
    {
        TableData pojo = null;

        if (resultSet != null)
        {
            TableData.TableDataBuilder builder = TableData.builder();
            builder.columns(getColumns(resultSet));
            builder.rows(createStream(resultSet, statement, connection));
            pojo = builder.build();
        }

        return pojo;
    }

    private Iterable<Column> getColumns(ResultSet resultSet)
    {
        return getColumnIndexes(resultSet).mapToObj(toColumn(resultSet)).collect(ImmutableList.toImmutableList());
    }

    private IntFunction<Column> toColumn(ResultSet resultSet)
    {
        return columnNumber -> jdbcColumnConverter.toPojo(columnNumber, resultSet);
    }

    private Stream<Iterable<Cell>> createStream(ResultSet resultSet, Statement statement, Connection connection)
    {
        Spliterator<Iterable<Cell>> spliterator = new Spliterators.AbstractSpliterator<Iterable<Cell>>(Long.MAX_VALUE,
            Spliterator.ORDERED)
        {
            @Override
            public boolean tryAdvance(Consumer<? super Iterable<Cell>> action)
            {
                try
                {
                    if (resultSet.next())
                    {
                        action.accept(readRow(resultSet));
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                catch (SQLException e)
                {
                    throw new StoreLayerException("Error advancing result set", e);
                }
            }
        };
        return StreamSupport.stream(spliterator, false).onClose(() -> {
            try (
                Connection tryConnection = connection;
                Statement tryStatement = statement;
                ResultSet tryResultSet = resultSet)
            {
                log.debug("Closing {}, {} and {}", tryResultSet, tryStatement, tryConnection);
            }
            catch (SQLException e)
            {
                throw new StoreLayerException("Error closing result set", e);
            }
        });
    }

    private Iterable<Cell> readRow(ResultSet resultSet)
    {
        return getColumnIndexes(resultSet).mapToObj(toCell(resultSet)).collect(ImmutableList.toImmutableList());
    }

    private IntFunction<Cell> toCell(ResultSet resultSet)
    {
        return columnNumber -> jdbcCellConverter.toPojo(columnNumber, resultSet);
    }

    private IntStream getColumnIndexes(ResultSet resultSet)
    {
        return IntStream.rangeClosed(1, getColumnCount(resultSet));
    }

    private int getColumnCount(ResultSet resultSet)
    {
        try
        {
            return resultSet.getMetaData().getColumnCount();
        }
        catch (SQLException e)
        {
            throw new StoreLayerException("Error reading column count", e);
        }
    }
}