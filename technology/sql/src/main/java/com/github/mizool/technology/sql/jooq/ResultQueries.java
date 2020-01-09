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

import java.util.Optional;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

import org.jooq.ResultQuery;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.exception.DataAccessException;

import com.github.mizool.core.exception.StoreLayerException;

@UtilityClass
public class ResultQueries
{
    public <R extends TableRecord<R>> Optional<R> read(ResultQuery<R> resultQuery, Table<R> table)
    {
        try
        {
            return resultQuery.fetchOptional();
        }
        catch (DataAccessException e)
        {
            throw new StoreLayerException("Error fetching optional " + table.getName(), e);
        }
    }

    /**
     * @return A live {@code Stream} of the result data. The underlying connection is still open. You have to make sure
     * the {@code Stream} is properly closed, either by <b>always</b> consuming it fully (risky due to exceptions) or
     * preferrably by consuming it inside a <i>try-with-resources</i></i> block.
     */
    public <R extends TableRecord<R>> Stream<R> list(ResultQuery<R> resultQuery, Table<R> table)
    {
        try
        {
            return resultQuery.stream();
        }
        catch (DataAccessException e)
        {
            throw new StoreLayerException("Error streaming " + table.getName(), e);
        }
    }
}