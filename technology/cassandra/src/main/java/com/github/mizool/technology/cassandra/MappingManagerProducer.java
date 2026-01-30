package com.github.mizool.technology.cassandra;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

class MappingManagerProducer
{
    @Produces
    @Singleton
    public MappingManager produce(Session session)
    {
        return new MappingManager(session);
    }
}
