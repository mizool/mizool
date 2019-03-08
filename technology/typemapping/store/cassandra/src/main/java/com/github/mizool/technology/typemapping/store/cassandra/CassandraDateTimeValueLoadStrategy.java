package com.github.mizool.technology.typemapping.store.cassandra;

import java.time.Instant;
import java.time.ZoneId;

import javax.inject.Inject;

import com.datastax.driver.core.Row;
import com.github.mizool.core.converter.ZonedDateTimeConverter;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraDateTimeValueLoadStrategy extends AbstractCassandraValueLoadStrategy
{
    private final ZonedDateTimeConverter zonedDateTimeConverter;

    @Inject
    public CassandraDateTimeValueLoadStrategy(ZonedDateTimeConverter zonedDateTimeConverter)
    {
        super(com.datastax.driver.core.DataType.timestamp(), DataType.DATE_TIME);
        this.zonedDateTimeConverter = zonedDateTimeConverter;
    }

    @Override
    public Object loadValue(ZoneId zoneId, String columnName, Row row)
    {
        return zonedDateTimeConverter.fromInstant(row.get(columnName, Instant.class), zoneId);
    }
}