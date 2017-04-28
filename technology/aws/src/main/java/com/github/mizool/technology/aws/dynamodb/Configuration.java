package com.github.mizool.technology.aws.dynamodb;

import com.amazonaws.regions.Regions;

public class Configuration
{
    private static final String DEFAULT_REGION = Regions.EU_CENTRAL_1.name();

    public Regions getAwsRegion()
    {
        String region = System.getProperty("aws.region", DEFAULT_REGION);
        return Regions.valueOf(region);
    }

    public String getEndpoint()
    {
        return System.getProperty("aws.endpoint");
    }
}