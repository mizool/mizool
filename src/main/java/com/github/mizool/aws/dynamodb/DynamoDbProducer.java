package com.github.mizool.aws.dynamodb;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DynamoDbProducer
{
    private final AmazonDynamoDBClient client;

    public DynamoDbProducer()
    {
        client = new AmazonDynamoDBClient();
        client.withRegion(new Configuration().getAwsRegion());
    }

    @Produces
    @Singleton
    public DynamoDB produce()
    {
        DynamoDB documentApi = new DynamoDB(client);
        return documentApi;
    }
}