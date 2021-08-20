package com.github.mizool.technology.cassandra;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

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
