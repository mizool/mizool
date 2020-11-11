package com.github.mizool.technology.gson;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.mizool.core.exception.BadRequestException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestStrictDeserializer
{

    private Gson gson;

    @BeforeMethod
    public void configure()
    {
        gson = new GsonBuilder().registerTypeAdapter(TestJsonObject.class, new StrictDeserializer()).create();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    private static class TestJsonObject
    {
        private String content;
        private String moreContent;
    }

    @Test
    public void testDeserializeWithAllParameters()
    {
        String inputJson = "{\"content\":\"test\",\"moreContent\":\"data\"}";
        TestJsonObject testJsonObject = new TestJsonObject("test", "data");
        TestJsonObject result = gson.fromJson(inputJson, TestJsonObject.class);
        assertThat(result).isEqualTo(testJsonObject);
    }

    @Test
    public void testDeserializeWithMissingParameters()
    {
        String inputJson = "{\"content\":\"test\"}";
        TestJsonObject testJsonObject = new TestJsonObject("test", null);
        TestJsonObject result = gson.fromJson(inputJson, TestJsonObject.class);
        assertThat(result).isEqualTo(testJsonObject);
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testDeserializeWithTooManyParameters()
    {
        String inputJson = "{\"content\":\"test\",\"moreContent\":\"data\", \"tooMany\":\"parameters\"}";
        TestJsonObject result = gson.fromJson(inputJson, TestJsonObject.class);
    }
}
