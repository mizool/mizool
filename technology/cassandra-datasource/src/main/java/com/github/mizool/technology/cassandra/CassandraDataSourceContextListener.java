package com.github.mizool.technology.cassandra;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class CassandraDataSourceContextListener implements ServletContextListener
{
    @Resource(name = "cassandra")
    private CassandraDataSource cassandraDataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        cassandraDataSource.initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        cassandraDataSource.destroy();
    }
}