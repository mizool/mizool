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

import lombok.experimental.UtilityClass;

import org.jooq.Condition;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;

import com.github.mizool.core.exception.StoreLayerException;

@UtilityClass
public class Records
{
    public <R extends TableRecord<R>> void insert(
        R record, Table<R> table, MizoolConnectionProvider mizoolConnectionProvider)
    {
        try
        {
            int insertedRows = DslContexts.obtain(mizoolConnectionProvider).executeInsert(record);
            if (insertedRows != 1)
            {
                throw new StoreLayerException("No row was inserted into " + table.getName());
            }
        }
        catch (DataAccessException e)
        {
            throw new StoreLayerException("Error inserting into " + table.getName(), e);
        }
    }

    public <R extends TableRecord<R>> void upsert(
        R record, Table<R> table, MizoolConnectionProvider mizoolConnectionProvider)
    {
        try
        {
            int upsertedRows = DslContexts.obtain(mizoolConnectionProvider)
                .insertInto(table)
                .set(record)
                .onDuplicateKeyUpdate()
                .set(record)
                .execute();
            if (upsertedRows != 1)
            {
                throw new StoreLayerException("No row was upserted to " + table.getName());
            }
        }
        catch (DataAccessException e)
        {
            throw new StoreLayerException("Error upserting to " + table.getName(), e);
        }
    }

    public <R extends TableRecord<R>> void update(
        R record, Table<R> table, Condition condition, MizoolConnectionProvider mizoolConnectionProvider)
    {
        try
        {
            int updatedRows = DslContexts.obtain(mizoolConnectionProvider)
                .update(table)
                .set(record)
                .where(condition)
                .execute();
            if (updatedRows != 1)
            {
                throw new StoreLayerException("No row was updated in " + table.getName());
            }
        }
        catch (DataAccessException e)
        {
            throw new StoreLayerException("Error updating " + table.getName(), e);
        }
    }

    public <R extends UpdatableRecord<R>> int delete(
        R record, Table<R> table, MizoolConnectionProvider mizoolConnectionProvider)
    {
        try
        {
            return DslContexts.obtain(mizoolConnectionProvider).executeDelete(record);
        }
        catch (DataAccessException e)
        {
            throw new StoreLayerException("Error deleting from " + table.getName(), e);
        }
    }
}