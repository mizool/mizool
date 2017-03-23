package com.github.mizool.aws.dynamodb;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class AmazonDynamoDbProducer
{
    private final AmazonDynamoDBClient client;

    @Inject
    public AmazonDynamoDbProducer(Configuration configuration)
    {
        client = new AmazonDynamoDBClient();
        client.withRegion(configuration.getAwsRegion());
    }

    @Produces
    @Singleton
    public AmazonDynamoDB produce()
    {
        return client;
    }
}