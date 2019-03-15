package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.github.mizool.technology.typemapping.business.DataType;
import com.google.common.collect.ImmutableList;

class JdbcBooleanValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    public JdbcBooleanValueLoadStrategy()
    {
        super(ImmutableList.of(Types.BOOLEAN), DataType.BOOLEAN);
    }

    @Override
    public Object loadValue( String columnName, ResultSet resultSet) throws SQLException
    {
        Boolean result = resultSet.getBoolean(columnName);
        if (resultSet.wasNull())
        {
            result = null;
        }
        return result;
    }
}