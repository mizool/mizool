package com.github.mizool.technology.cassandra;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

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
