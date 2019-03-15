package com.github.mizool.technology.tableaccess.store.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Inject;
import javax.sql.DataSource;

import com.github.mizool.core.exception.StoreLayerException;
import com.github.mizool.technology.tableaccess.business.TableData;

public class JdbcQueryExecuteStore
{
    private final JdbcTableDataConverter converter;

    @Inject
    protected JdbcQueryExecuteStore(JdbcTableDataConverter converter)
    {
        this.converter = converter;
    }

    public TableData execute(String query, DataSource dataSource)
    {
        /*
         * Statement and connection are created here but passed on to the converter to be closed there later.
         * This is necessary since the resultSet, that is being worked on in the converter,
         * is closed as soon as statement or connection are closed here
         */
        Connection connection = obtainConnection(dataSource);
        Statement statement = obtainStatement(connection);

        try
        {
            ResultSet resultSet = statement.executeQuery(query);
            return converter.toPojo(resultSet, statement, connection);
        }
        catch (SQLException | StoreLayerException e)
        {
            /*
             * For successful query execution, connection and statement have to be left open as explained above. However, when an
             * exception occurs, we have to close them immediately to avoid draining the pool. For details, see MJX-1828.
             */
            try
            {
                closeStatement(statement);
            }
            finally
            {
                closeConnection(connection);
            }
            throw new StoreLayerException("could not execute query:\n" + query, e);
        }
    }

    private Connection obtainConnection(DataSource dataSource)
    {
        Connection connection;
        try
        {
            connection = dataSource.getConnection();
        }
        catch (SQLException e)
        {
            throw new StoreLayerException("could not get connection", e);
        }
        return connection;
    }

    private Statement obtainStatement(Connection connection)
    {
        Statement statement;
        try
        {
            statement = connection.createStatement();
        }
        catch (SQLException e)
        {
            closeConnection(connection);
            throw new StoreLayerException("could not create statement", e);
        }
        return statement;
    }

    private void closeStatement(Statement statement)
    {
        try
        {
            statement.close();
        }
        catch (SQLException e)
        {
            throw new StoreLayerException("could not close statement", e);
        }
    }

    private void closeConnection(Connection connection)
    {
        try
        {
            connection.close();
        }
        catch (SQLException e)
        {
            throw new StoreLayerException("could not close connection", e);
        }
    }
}