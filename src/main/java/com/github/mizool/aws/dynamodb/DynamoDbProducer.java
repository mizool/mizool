package com.github.mizool.aws.dynamodb;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DynamoDbProducer
{
    private final AmazonDynamoDBClient client;

    public DynamoDbProducer()
    {
        client = new AmazonDynamoDBClient();
        client.withRegion(Regions.EU_CENTRAL_1);
    }

    @Produces
    @Singleton
    public DynamoDB produce()
    {
        DynamoDB documentApi = new DynamoDB(client);
        return documentApi;
    }
}