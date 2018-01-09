package com.github.mizool.technology.cassandra;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

public class CassandraDataSourceProducer
{
    @Resource(name = "cassandra")
    private CassandraDataSource cassandraDataSource;

    @Produces
    @Singleton
    public CassandraDataSource produce()
    {
        return cassandraDataSource;
    }

    void initialize(@Observes @Initialized(ApplicationScoped.class) Object initializedObject)
    {
        cassandraDataSource.initialize();
    }


}