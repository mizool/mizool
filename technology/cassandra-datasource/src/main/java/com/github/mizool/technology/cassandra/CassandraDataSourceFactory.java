package com.github.mizool.technology.cassandra;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

public class CassandraDataSourceFactory implements ObjectFactory
{
    @Override
    public Object getObjectInstance(
        Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception
    {
        CassandraDataSource cassandraDataSource = new CassandraDataSource();
        // Customize the bean properties from our attributes
        Reference ref = (Reference) obj;
        Enumeration addrs = ref.getAll();
        while (addrs.hasMoreElements())
        {
            RefAddr addr = (RefAddr) addrs.nextElement();
            String parameterName = addr.getType();
            String value = (String) addr.getContent();
            if (parameterName.equals("addresses"))
            {
                cassandraDataSource.setAddresses(value);
            }
        }
        return cassandraDataSource;
    }
}