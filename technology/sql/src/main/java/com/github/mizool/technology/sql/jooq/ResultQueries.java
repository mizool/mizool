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
     * preferrably by consuming it inside a <i>try-with-resources</i> block.
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
