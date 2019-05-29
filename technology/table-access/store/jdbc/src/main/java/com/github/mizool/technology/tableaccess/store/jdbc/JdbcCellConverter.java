package com.github.mizool.technology.tableaccess.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.github.mizool.core.exception.StoreLayerException;
import com.github.mizool.technology.tableaccess.business.Cell;
import com.github.mizool.technology.tableaccess.business.Column;
import com.github.mizool.technology.typemapping.store.jdbc.JdbcValueLoadStrategyResolver;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
class JdbcCellConverter
{
    private final JdbcColumnConverter jdbcColumnConverter;
    private final JdbcValueLoadStrategyResolver jdbcValueLoadStrategyResolver;

    public Cell toPojo(int columnNumber, ResultSet resultSet)
    {
        Cell pojo = null;

        if (resultSet != null)
        {
            Column column = jdbcColumnConverter.toPojo(columnNumber, resultSet);
            Cell.CellBuilder builder = Cell.builder();
            builder.column(column);
            try
            {
                builder.value(jdbcValueLoadStrategyResolver.resolve(resultSet.getMetaData().getColumnType(columnNumber))
                    .loadValue(column.getName(), resultSet));
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