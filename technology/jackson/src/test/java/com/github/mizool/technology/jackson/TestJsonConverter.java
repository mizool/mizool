/*
 * Copyright 2019-2020 incub8 Software Labs GmbH
 * Copyright 2019-2020 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.technology.jackson;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TestJsonConverter
{
    @Data
    private static class Pojo
    {
        /**
         * Allows instantiation by Jackson.
         */
        @VisibleForTesting
        private Pojo()
        {
        }

        @Builder(toBuilder = true)
        private Pojo(Integer field)
        {
            this.field = field;
        }

        private Integer field;
    }

    private JsonConverter jsonConverter;

    @BeforeMethod
    public void setUp()
    {
        ObjectMapper objectMapper = new ObjectMapper();

        jsonConverter = new JsonConverter(objectMapper);
    }

    @Test
    public void testStringToRecord()
    {
        String pojo = "Foo";
        String expected = "\"Foo\"";

        String actual = jsonConverter.toRecord(pojo);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testIntegerToRecord()
    {
        Integer pojo = 1;
        String expected = "1";

        String actual = jsonConverter.toRecord(pojo);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testPojoToRecord()
    {
        Pojo pojo = Pojo.builder()
            .field(1)
            .build();
        String expected = "{\"field\":1}";

        String actual = jsonConverter.toRecord(pojo);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCollectionToRecord()
    {
        Collection<Integer> pojo = ImmutableList.of(1, 3, 3, 7);
        String expected = "[1,3,3,7]";

        String actual = jsonConverter.toRecord(pojo);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testMapToRecord()
    {
        Map<Integer, Integer> pojo = ImmutableMap.of(1, 2, 3, 4);
        String expected = "{\"1\":2,\"3\":4}";

        String actual = jsonConverter.toRecord(pojo);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testStringToPojo()
    {
        String record = "\"Foo\"";
        String expected = "Foo";

        TypeReference<String> typeReference = new TypeReference<String>()
        {
        };
        String actual = jsonConverter.toPojo(record, typeReference);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testIntegerToPojo()
    {
        String record = "1";
        Integer expected = 1;

        TypeReference<Integer> typeReference = new TypeReference<Integer>()
        {
        };
        Integer actual = jsonConverter.toPojo(record, typeReference);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testRecordToPojo()
    {
        String record = "{\r\n  \"field\" : 1\r\n}";
        Pojo expected = Pojo.builder()
            .field(1)
            .build();

        TypeReference<Pojo> typeReference = new TypeReference<Pojo>()
        {
        };
        Pojo actual = jsonConverter.toPojo(record, typeReference);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCollectionToPojo()
    {
        String record = "[ 1, 3, 3, 7 ]";
        Collection<Integer> expected = ImmutableList.of(1, 3, 3, 7);

        TypeReference<Collection<Integer>> typeReference = new TypeReference<Collection<Integer>>()
        {
        };
        Collection<Integer> actual = jsonConverter.toPojo(record, typeReference);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testMapToPojo()
    {
        String record = "{\r\n  \"1\" : 2,\r\n  \"3\" : 4\r\n}";
        Map<Integer, Integer> expected = ImmutableMap.of(1, 2, 3, 4);

        TypeReference<Map<Integer, Integer>> typeReference = new TypeReference<Map<Integer, Integer>>()
        {
        };
        Map<Integer, Integer> actual = jsonConverter.toPojo(record, typeReference);

        assertThat(actual).isEqualTo(expected);
    }
}