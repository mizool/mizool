/**
 *  Copyright 2017 incub8 Software Labs GmbH
 *  Copyright 2017 protel Hotelsoftware GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.mizool.technology.cassandra;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

@Slf4j
public class CassandraDataSource
{
    @Getter
    @Setter
    private String addresses;

    @Getter
    private Cluster cluster;

    @Getter
    private Session session;

    private MappingManager mappingManager;

    public void initialize()
    {
        log.debug("initializing CassandraDataSource");
        this.cluster = Cluster.builder()
            .addContactPoints(parseAddressString(addresses))
            .withQueryOptions(getQueryOptions())
            .withMaxSchemaAgreementWaitSeconds(300)
            .build()
            .init();
        this.session = cluster.connect();
        this.mappingManager = new MappingManager(session);
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

    public void destroy()
    {
        log.debug("destroying CassandraDataSource");
        closeSession();
        closeCluster();
    }

    private void closeSession()
    {
        session.close();
        session = null;
    }

    private void closeCluster()
    {
        cluster.close();
        cluster = null;
    }

    public <T> Mapper<T> mapper(Class<T> clazz)
    {
        return mappingManager.mapper(clazz);
    }
}