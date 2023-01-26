package com.github.mizool.technology.cassandra;

import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import com.datastax.driver.core.Cluster;

class ClusterProducer
{
    @Produces
    @Singleton
    public Cluster produce(Cluster.Initializer initializer)
    {
        return Cluster.buildFrom(initializer).init();
    }

    public void dispose(@Disposes Cluster cluster)
    {
        cluster.close();
    }
}
