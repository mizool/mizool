package com.github.mizool.technology.sql.jooq;

import lombok.experimental.UtilityClass;

import org.jooq.CloseableDSLContext;
import org.jooq.Condition;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;

import com.github.mizool.core.exception.ConflictingEntityException;
import com.github.mizool.core.exception.ObjectNotFoundException;
import com.github.mizool.core.exception.StoreLayerException;

@UtilityClass
public class Records
{
    public <R extends TableRecord<R>> void insert(
        R entity, Table<R> table, MizoolConnectionProvider mizoolConnectionProvider)
    {
        try (CloseableDSLContext dslContext = DslContexts.obtain(mizoolConnectionProvider))
        {
            int insertedRows = dslContext.insertInto(table)
                .set(entity)
                .onDuplicateKeyIgnore()
                .execute();
            if (insertedRows != 1)
            {
                throw new ConflictingEntityException("No row was inserted into " + table.getName());
            }
        }
        catch (DataAccessException e)
        {
            throw new StoreLayerException("Error inserting into " + table.getName(), e);
        }
    }

    public <R extends TableRecord<R>> void upsert(
        R entity, Table<R> table, MizoolConnectionProvider mizoolConnectionProvider)
    {
        try (CloseableDSLContext dslContext = DslContexts.obtain(mizoolConnectionProvider))
        {
            int upsertedRows = dslContext.insertInto(table)
                .set(entity)
                .onDuplicateKeyUpdate()
                .set(entity)
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
        R entity, Table<R> table, Condition condition, MizoolConnectionProvider mizoolConnectionProvider)
    {
        try (CloseableDSLContext dslContext = DslContexts.obtain(mizoolConnectionProvider))
        {
            int updatedRows = dslContext.update(table)
                .set(entity)
                .where(condition)
                .execute();
            if (updatedRows != 1)
            {
                throw new ObjectNotFoundException("No row was updated in " + table.getName());
            }
        }
        catch (DataAccessException e)
        {
            throw new StoreLayerException("Error updating " + table.getName(), e);
        }
    }

    public <R extends UpdatableRecord<R>> void delete(
        R entity, Table<R> table, MizoolConnectionProvider mizoolConnectionProvider)
    {
        try (CloseableDSLContext dslContext = DslContexts.obtain(mizoolConnectionProvider))
        {
            int deletedRows = dslContext.executeDelete(entity);
            if (deletedRows != 1)
            {
                throw new ObjectNotFoundException("No row was deleted in " + table.getName());
            }
        }
        catch (DataAccessException e)
        {
            throw new StoreLayerException("Error deleting from " + table.getName(), e);
        }
    }
}
