package com.github.mizool.technology.cassandra;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.extras.codecs.jdk8.InstantCodec;
import com.datastax.driver.extras.codecs.jdk8.LocalDateCodec;
import com.datastax.driver.extras.codecs.jdk8.LocalTimeCodec;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

class ClusterProducer
{
    private static final String CASSANDRA_CONTACT_POINTS_PROPERTY_NAME = "mizool.cassandra.contactpoints";

    @Produces
    @Singleton
    public Cluster produce()
    {
        String addresses = System.getProperty(CASSANDRA_CONTACT_POINTS_PROPERTY_NAME, "127.0.0.1");
        Cluster cluster = Cluster.builder()
            .addContactPoints(parseAddressString(addresses))
            .withQueryOptions(getQueryOptions())
            .withMaxSchemaAgreementWaitSeconds(300)
            .build()
            .init();
        registerJdk8TimeCodecs(cluster);
        return cluster;
    }

    private String[] parseAddressString(String addresses)
    {
        return Iterables.toArray(Splitter.on(",").trimResults().split(addresses), String.class);
    }

    private QueryOptions getQueryOptions()
    {
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setConsistencyLevel(ConsistencyLevel.ALL);

        return queryOptions;
    }

    private void registerJdk8TimeCodecs(Cluster cluster)
    {
        cluster.getConfiguration()
            .getCodecRegistry()
            .register(InstantCodec.instance)
            .register(LocalDateCodec.instance)
            .register(LocalTimeCodec.instance);
    }

    public void dispose(@Disposes Cluster cluster)
    {
        cluster.close();
    }
}