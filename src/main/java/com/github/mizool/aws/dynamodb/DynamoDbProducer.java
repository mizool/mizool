package com.github.mizool.aws.dynamodb;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DynamoDbProducer
{
    private final AmazonDynamoDBClient client;

    @Inject
    public DynamoDbProducer(Configuration configuration)
    {
        client = new AmazonDynamoDBClient();
        client.withRegion(configuration.getAwsRegion());
    }

    @Produces
    @Singleton
    public DynamoDB produce()
    {
        DynamoDB documentApi = new DynamoDB(client);
        return documentApi;
    }
}