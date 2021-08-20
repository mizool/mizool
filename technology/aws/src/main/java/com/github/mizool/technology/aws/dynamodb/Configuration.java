package com.github.mizool.technology.aws.dynamodb;

import com.amazonaws.regions.Regions;
import com.github.mizool.core.configuration.Config;
import com.github.mizool.core.configuration.PropertyNode;
import com.github.mizool.core.configuration.Value;

public class Configuration
{
    private static final PropertyNode CONFIG = Config.systemProperties()
        .child("aws");

    private static final Value<String> ENDPOINT = CONFIG.child("endpoint")
        .stringValue();

    private static final Value<Regions> REGION = CONFIG.child("region")
        .convertedValue(Regions::valueOf);

    public Regions getAwsRegion()
    {
        return REGION.read()
            .orElse(Regions.EU_CENTRAL_1);
    }

    public String getEndpoint()
    {
        return ENDPOINT.obtain();
    }
}
