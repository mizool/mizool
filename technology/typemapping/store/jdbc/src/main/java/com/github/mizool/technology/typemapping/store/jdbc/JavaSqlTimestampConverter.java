package com.github.mizool.technology.typemapping.store.jdbc;

import java.sql.Timestamp;
import java.time.Instant;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
class JavaSqlTimestampConverter
{
    public Instant toPojo(Timestamp record)
    {
        Instant pojo = null;

        if (record != null)
        {
            pojo = Instant.ofEpochMilli(record.getTime());
        }

        return pojo;
    }
}