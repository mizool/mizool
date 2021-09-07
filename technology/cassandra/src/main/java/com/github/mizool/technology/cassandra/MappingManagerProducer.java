package com.github.mizool.technology.cassandra;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;

class MappingManagerProducer
{
    @Produces
    @Singleton
    public MappingManager produce(Session session)
    {
        return new MappingManager(session);
    }
}
