package com.github.mizool.technology.aws.dynamodb;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDbMapperProducer
{
    private final AmazonDynamoDB client;

    @Inject
    public DynamoDbMapperProducer(AmazonDynamoDB client)
    {
        this.client = client;
    }

    @Produces
    @Singleton
    public DynamoDBMapper produce()
    {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        return mapper;
    }
}
