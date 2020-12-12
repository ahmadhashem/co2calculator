package com.sap.co2calculator.service.impl;

import com.sap.co2calculator.exception.ConfigurationException;
import org.junit.Test;


import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class ConfigurationProviderImplTest
{
    @Test
    public void testReadConfigFile() throws Exception
    {
        final String TOKEN_NAME = "API_TOKEN";
        withEnvironmentVariable(TOKEN_NAME, "1234567890")
            .execute(() -> {
                ConfigurationProviderImpl provider = new ConfigurationProviderImpl("config.properties", "API_TOKEN");
                assertThat(provider.getConfig("GET_CITY_COORDINATES_API_URL"), equalTo("https://api.openrouteservice.org/geocode/search"));
                assertThat(provider.getConfig("GET_DISTANCE_API_URL"), equalTo("https://api.openrouteservice.org/v2/matrix/driving-car"));
                assertThat(provider.getConfig(TOKEN_NAME), equalTo("1234567890"));
            });
    }

    @Test(expected = ConfigurationException.class)
    public void testReadInvalidFile() throws Exception
    {
        new ConfigurationProviderImpl("invalid-file.properties", "API_TOKEN");
    }

    @Test(expected = ConfigurationException.class)
    public void testTokenNameNotDefined() throws Exception
    {
        new ConfigurationProviderImpl("invalid-file.properties", "API_TOKEN");
    }

}