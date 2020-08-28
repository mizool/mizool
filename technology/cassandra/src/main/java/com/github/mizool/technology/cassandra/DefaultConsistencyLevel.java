/*
 * Copyright 2017-2020 incub8 Software Labs GmbH
 * Copyright 2017-2020 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.technology.cassandra;

import lombok.experimental.UtilityClass;

import com.datastax.driver.core.ConsistencyLevel;
import com.github.mizool.core.configuration.Config;
import com.github.mizool.core.configuration.PropertyNode;

@UtilityClass
public class DefaultConsistencyLevel
{
    private final PropertyNode CONFIG = Config.systemProperties().child(DefaultConsistencyLevel.class.getName());

    public final ConsistencyLevel READ = CONFIG.child("READ")
        .convertedValue(ConsistencyLevel::valueOf)
        .read()
        .orElse(ConsistencyLevel.LOCAL_QUORUM);

    public final ConsistencyLevel WRITE = CONFIG.child("WRITE")
        .convertedValue(ConsistencyLevel::valueOf)
        .read()
        .orElse(ConsistencyLevel.LOCAL_QUORUM);
}
