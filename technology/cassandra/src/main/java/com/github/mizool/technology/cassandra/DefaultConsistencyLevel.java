package com.github.mizool.technology.cassandra;

import lombok.experimental.UtilityClass;

import com.datastax.driver.core.ConsistencyLevel;
import com.github.mizool.core.configuration.Config;
import com.github.mizool.core.configuration.PropertyNode;

@UtilityClass
public class DefaultConsistencyLevel
{
    private final PropertyNode CONFIG = Config.systemProperties()
        .child(DefaultConsistencyLevel.class.getName());

    public final ConsistencyLevel READ = CONFIG.child("READ")
        .convertedValue(ConsistencyLevel::valueOf)
        .read()
        .orElse(ConsistencyLevel.LOCAL_QUORUM);

    public final ConsistencyLevel WRITE = CONFIG.child("WRITE")
        .convertedValue(ConsistencyLevel::valueOf)
        .read()
        .orElse(ConsistencyLevel.LOCAL_QUORUM);
}
