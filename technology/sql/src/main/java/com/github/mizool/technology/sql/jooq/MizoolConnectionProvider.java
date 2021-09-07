package com.github.mizool.technology.sql.jooq;

import lombok.Getter;
import lombok.NonNull;

import org.jooq.ConnectionProvider;
import org.jooq.SQLDialect;

import com.github.mizool.core.exception.CodeInconsistencyException;

public abstract class MizoolConnectionProvider implements ConnectionProvider
{
    @Getter
    private final SQLDialect sqlDialect;

    protected MizoolConnectionProvider(@NonNull String driverClassName, @NonNull SQLDialect sqlDialect)
    {
        this.sqlDialect = sqlDialect;
        System.setProperty("org.jooq.no-logo", "true");
        loadDriver(driverClassName);
    }

    private void loadDriver(String driverClassName)
    {
        try
        {
            Class.forName(driverClassName);
        }
        catch (ClassNotFoundException e)
        {
            throw new CodeInconsistencyException("JDBC driver '" + driverClassName + "' is missing on classpath", e);
        }
    }
}
