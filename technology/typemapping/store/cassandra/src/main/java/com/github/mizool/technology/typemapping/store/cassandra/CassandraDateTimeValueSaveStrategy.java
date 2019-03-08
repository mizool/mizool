package com.github.mizool.technology.typemapping.store.cassandra;

import java.time.ZonedDateTime;

import javax.inject.Inject;

import com.github.mizool.core.converter.ZonedDateTimeConverter;
import com.github.mizool.technology.typemapping.business.DataType;

class CassandraDateTimeValueSaveStrategy extends AbstractCassandraValueSaveStrategy
{
    private final ZonedDateTimeConverter zonedDateTimeConverter;

    @Inject
    public CassandraDateTimeValueSaveStrategy(ZonedDateTimeConverter zonedDateTimeConverter)
    {
        super(DataType.DATE_TIME, com.datastax.driver.core.DataType.timestamp());
        this.zonedDateTimeConverter = zonedDateTimeConverter;
    }

    @Override
    protected Object convertValue(Object value)
    {
        return zonedDateTimeConverter.toInstant((ZonedDateTime) value);
    }
}