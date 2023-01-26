package com.github.mizool.technology.aws.dynamodb;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DynamoDbProducer
{
    private final AmazonDynamoDB client;

    @Inject
    public DynamoDbProducer(AmazonDynamoDB client)
    {
        this.client = client;
    }

    @Produces
    @Singleton
    public DynamoDB produce()
    {
        DynamoDB documentApi = new DynamoDB(client);
        return documentApi;
    }
}