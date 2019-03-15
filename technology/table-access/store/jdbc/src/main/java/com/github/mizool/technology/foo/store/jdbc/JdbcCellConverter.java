package com.github.mizool.technology.foo.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.github.mizool.core.exception.StoreLayerException;
import com.github.mizool.technology.foo.business.Cell;
import com.github.mizool.technology.foo.business.Column;
import com.github.mizool.technology.typemapping.store.jdbc.JdbcValueLoadStrategyResolver;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
class JdbcCellConverter
{
    private final JdbcColumnConverter prestoColumnConverter;
    private final JdbcValueLoadStrategyResolver jdbcValueLoadStrategyResolver;

    public Cell toPojo(ZoneId zoneId, int columnNumber, ResultSet resultSet)
    {
        Cell pojo = null;

        if (resultSet != null)
        {
            Column column = prestoColumnConverter.toPojo(columnNumber, resultSet);
            Cell.CellBuilder builder = Cell.builder();
            builder.column(column);
            try
            {
                builder.value(jdbcValueLoadStrategyResolver.resolve(resultSet.getMetaData().getColumnType(columnNumber))
                    .loadValue(zoneId, column.getName(), resultSet));
            }
            catch (SQLException e)
            {
                throw new StoreLayerException("Error reading cell value in column " + columnNumber, e);
            }
            pojo = builder.build();
        }

        return pojo;
    }
}