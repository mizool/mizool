package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.github.mizool.technology.typemapping.business.DataType;
import com.google.common.collect.ImmutableList;

class JdbcStringValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    public JdbcStringValueLoadStrategy()
    {
        super(ImmutableList.of(Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR, Types.NVARCHAR), DataType.STRING);
    }

    @Override
    public Object loadValue(String columnName, ResultSet resultSet) throws SQLException
    {
        return resultSet.getString(columnName);
    }
}