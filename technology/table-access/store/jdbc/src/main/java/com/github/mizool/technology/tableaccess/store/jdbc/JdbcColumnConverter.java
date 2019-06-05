package com.github.mizool.technology.tableaccess.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.github.mizool.core.exception.StoreLayerException;
import com.github.mizool.technology.tableaccess.business.Column;
import com.github.mizool.technology.typemapping.business.DataType;
import com.github.mizool.technology.typemapping.store.jdbc.JdbcValueLoadStrategyResolver;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
class JdbcColumnConverter
{
    private final JdbcValueLoadStrategyResolver jdbcValueLoadStrategyResolver;

    public Column toPojo(int columnNumber, ResultSet resultSet)
    {
        Column pojo = null;

        if (resultSet != null)
        {
            Column.ColumnBuilder builder = Column.builder();
            builder.name(getColumnLabel(columnNumber, resultSet));
            builder.type(getColumnType(columnNumber, resultSet));
            pojo = builder.build();
        }

        return pojo;
    }

    private String getColumnLabel(int columnNumber, ResultSet resultSet)
    {
        try
        {
            return resultSet.getMetaData().getColumnLabel(columnNumber);
        }
        catch (SQLException e)
        {
            throw new StoreLayerException("Error reading column " + columnNumber + " label", e);
        }
    }

    private DataType getColumnType(int columnNumber, ResultSet resultSet)
    {
        try
        {
            return jdbcValueLoadStrategyResolver.resolve(resultSet.getMetaData().getColumnType(columnNumber))
                .getTargetDataType();
        }
        catch (SQLException e)
        {
            throw new StoreLayerException("Error reading column " + columnNumber + " type", e);
        }
    }
}