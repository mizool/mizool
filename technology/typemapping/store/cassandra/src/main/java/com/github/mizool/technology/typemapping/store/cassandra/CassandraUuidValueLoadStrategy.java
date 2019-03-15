package com.github.mizool.technology.typemapping.store.cassandra;

import java.time.ZoneId;

import com.datastax.driver.core.Row;
import com.github.mizool.technology.typemapping.business.DataType;

/**
 * Although we don't support UUIDs in the core layer, we still need to to be able to load them as string, to be able to
 * transport the {@code row_uuid__} which is is returned when selecting {@code *}. Usually that column is then filtered
 * on a higher level.
 */
class CassandraUuidValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    public CassandraUuidValueLoadStrategy()
    {
        super(com.datastax.driver.core.DataType.uuid(), DataType.STRING);
    }

    @Override
    public Object loadValue(String columnName, Row row)
    {

        return row.getUUID(columnName).toString();
    }
}