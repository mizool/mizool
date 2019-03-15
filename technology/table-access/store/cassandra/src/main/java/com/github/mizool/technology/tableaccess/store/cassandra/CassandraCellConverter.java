package com.github.mizool.technology.tableaccess.store.cassandra;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.tableaccess.business.Cell;
import com.github.mizool.technology.tableaccess.business.Column;
import com.github.mizool.technology.typemapping.store.cassandra.CassandraValueLoadStrategyResolver;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
class CassandraCellConverter
{
    private final CassandraValueLoadStrategyResolver loadStrategyResolver;

    public Cell toPojo(Column column, Row row)
    {
        Cell pojo = null;

        if (row != null)
        {
            Cell.CellBuilder builder = Cell.builder();
            builder.column(column);
            builder.value(loadStrategyResolver.resolve(row.getColumnDefinitions().getType(column.getName()))
                .loadValue(column.getName(), row));
            pojo = builder.build();
        }

        return pojo;
    }
}