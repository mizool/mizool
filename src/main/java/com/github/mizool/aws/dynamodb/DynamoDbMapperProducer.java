/**
 *  Copyright 2017 incub8 Software Labs GmbH
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
package com.github.mizool.aws.dynamodb;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDbMapperProducer
{
    private final AmazonDynamoDBClient client;

    public DynamoDbMapperProducer()
    {
        client = new AmazonDynamoDBClient();
        client.withRegion(Regions.EU_CENTRAL_1);
    }

    @Produces
    @Singleton
    public DynamoDBMapper produce()
    {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        return mapper;
    }
}