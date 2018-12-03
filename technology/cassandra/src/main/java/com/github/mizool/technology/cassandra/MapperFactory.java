package com.github.mizool.technology.cassandra;

import com.datastax.driver.mapping.Mapper;

public interface MapperFactory<T>
{
    Mapper<T> getMapper(String keyspace);
}