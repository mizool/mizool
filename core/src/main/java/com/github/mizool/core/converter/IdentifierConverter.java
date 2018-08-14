/**
 * Copyright 2018 incub8 Software Labs GmbH
 * Copyright 2018 protel Hotelsoftware GmbH
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
package com.github.mizool.core.converter;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;

public class IdentifierConverter
{
    public String fromPojo(Identifier<?> pojo)
    {
        String result = null;

        if (pojo != null)
        {
            result = pojo.getValue();
        }

        return result;
    }

    public <T extends Identifiable<T>> Identifier<T> toPojo(String value, Class<T> pojoClass)
    {
        Identifier<T> pojo = null;

        if (value != null)
        {
            pojo = Identifier.forPojo(pojoClass).of(value);
        }

        return pojo;
    }
}
