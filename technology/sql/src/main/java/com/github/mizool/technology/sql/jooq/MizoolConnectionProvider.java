/*
 * Copyright 2020 incub8 Software Labs GmbH
 * Copyright 2020 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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