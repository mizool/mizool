package com.github.mizool.technology.cassandra;

import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

class SessionProducer
{
    @Produces
    @Singleton
    public Session produce(Cluster cluster)
    {
        return cluster.connect();
    }

    public void dispose(@Disposes Session session)
    {
        session.close();
    }
}
