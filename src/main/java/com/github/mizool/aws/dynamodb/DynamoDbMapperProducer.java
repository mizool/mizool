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