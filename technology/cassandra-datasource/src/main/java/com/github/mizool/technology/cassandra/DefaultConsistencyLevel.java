package com.github.mizool.technology.cassandra;

import com.datastax.driver.core.ConsistencyLevel;

public class DefaultConsistencyLevel
{
    private static final String READ_CONSISTENCY_PROPERTY_NAME = DefaultConsistencyLevel.class.getName() + ".READ";
    private static final String WRITE_CONSISTENCY_PROPERTY_NAME = DefaultConsistencyLevel.class.getName() + ".WRITE";

    public static final ConsistencyLevel READ = ConsistencyLevel.valueOf(
        System.getProperty(
            READ_CONSISTENCY_PROPERTY_NAME, "ONE"));
    public static final ConsistencyLevel WRITE = ConsistencyLevel.valueOf(
        System.getProperty(
            WRITE_CONSISTENCY_PROPERTY_NAME, "ONE"));
}