package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.github.mizool.technology.typemapping.business.DataType;

class JdbcStringFromNVCharValueLoadStrategy extends AbstractJdbcValueLoadStrategy
{
    public JdbcStringFromNVCharValueLoadStrategy()
    {
        super(Types.NVARCHAR, DataType.STRING);
    }

    @Override
    public Object loadValue(String columnName, ResultSet resultSet) throws SQLException
    {
        return resultSet.getString(columnName);
    }
}