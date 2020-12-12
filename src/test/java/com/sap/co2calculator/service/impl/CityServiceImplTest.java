package com.sap.co2calculator.service.impl;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.co2calculator.service.ConfigurationProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class CityServiceImplTest
{
    public static final ConfigurationProvider CONFIGURATION_PROVIDER = new ConfigurationProvider()
    {
        final Map<String, String> map = new HashMap()
        {
            {
                put("GET_CITY_COORDINATES_API_URL", "http://localhost:8089/");
                put("GET_DISTANCE_API_URL", "http://localhost:8089/");
                put("ORS_TOKEN", "123");
            }
        };

        @Override public String getConfig(String name)
        {
            return map.get(name);
        }
    };

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089); // No-args constructor defaults

    @Before
    public void setup() throws IOException
    {
        stubFor(any(urlPathEqualTo("/"))
                .withQueryParam("text", equalTo("Cairo"))
                .withQueryParam("api_key", equalTo("123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(readFile("cairo-response.json"))));

        // invalid city
        stubFor(any(urlPathEqualTo("/"))
                .withQueryParam("text", equalTo("Invalid"))
                .withQueryParam("api_key", equalTo("123"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("error")));

        // invalid key
        stubFor(any(urlPathEqualTo("/"))
                .withQueryParam("text", equalTo("Cairo"))
                .withQueryParam("api_key", equalTo("789"))
                .willReturn(aResponse()
                        .withStatus(403)
                        .withBody("error")));

        stubFor(any(urlPathEqualTo("/"))
                .withQueryParam("text", equalTo("Alexandria"))
                .withQueryParam("api_key", equalTo("123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(readFile("alexandria-response.json"))));

        stubFor(any(urlPathEqualTo("/"))
                .withQueryParam("text", equalTo("Error"))
                .withQueryParam("api_key", equalTo("123"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("error")));

        stubFor(any(urlPathEqualTo("/"))
                .withHeader("Authorization", equalTo("123"))
                .withRequestBody(equalToJson("{\"locations\":[[31.24967,30.06263],[29.95527,31.21564]],\"metrics\":[\"distance\"],\"units\":\"km\"}"))
                .willReturn(aResponse().withStatus(200).withBody(
                        readFile("distance-response.json"))));

    }

    private String readFile(String fileName) throws IOException
    {
        File file = new File(getClass().getClassLoader().getResource(fileName).getFile());
        return IOUtils.toString(new FileInputStream(file));
    }

    @After
    public void clean()
    {
        wireMockRule.shutdown();
    }

    @Test
    public void getDistance()
    {
        CityServiceImpl cityService = new CityServiceImpl(CONFIGURATION_PROVIDER);

        double distance = cityService.distanceFromTo("Cairo", "Alexandria");
        assertThat(distance, closeTo(223.27, 1));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidCity()
    {
        CityServiceImpl cityService = new CityServiceImpl(CONFIGURATION_PROVIDER);

        cityService.distanceFromTo("Invalid", "Alexandria");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidApiKey()
    {
        CityServiceImpl cityService = new CityServiceImpl(new ConfigurationProvider()
        {
            final Map<String, String> map = new HashMap()
            {
                {
                    put("GET_CITY_COORDINATES_API_URL", "http://localhost:8089/");
                    put("GET_DISTANCE_API_URL", "http://localhost:8089/");
                    put("ORS_TOKEN", "789");
                }
            };

            @Override public String getConfig(String name)
            {
                return map.get(name);
            }
        });

        cityService.distanceFromTo("Cairo", "Alexandria");
    }

    @Test(expected = RuntimeException.class)
    public void testErrorInApi()
    {
        CityServiceImpl cityService = new CityServiceImpl(CONFIGURATION_PROVIDER);
        cityService.distanceFromTo("Error", "Alexandria");
    }
}