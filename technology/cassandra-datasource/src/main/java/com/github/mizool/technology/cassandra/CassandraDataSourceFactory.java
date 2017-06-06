/**
 *  Copyright 2017 incub8 Software Labs GmbH
 *  Copyright 2017 protel Hotelsoftware GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
        Object object, Name name, Context context, Hashtable<?, ?> environment) throws Exception
    {
        CassandraDataSource cassandraDataSource = new CassandraDataSource();
        // Customize the bean properties from our attributes
        Reference reference = (Reference) object;
        Enumeration referenceAddresses = reference.getAll();
        while (referenceAddresses.hasMoreElements())
        {
            RefAddr referenceAddress = (RefAddr) referenceAddresses.nextElement();
            String parameterName = referenceAddress.getType();
            String value = (String) referenceAddress.getContent();
            if (parameterName.equals("addresses"))
            {
                cassandraDataSource.setAddresses(value);
            }
        }
        return cassandraDataSource;
    }
}