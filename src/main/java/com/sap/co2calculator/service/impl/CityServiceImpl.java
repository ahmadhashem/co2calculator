package com.sap.co2calculator.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.co2calculator.openrouteservice.pojo.getcoordinates.GetResponse;
import com.sap.co2calculator.openrouteservice.pojo.getdistance.request.GetDistanceRequest;
import com.sap.co2calculator.openrouteservice.pojo.getdistance.response.GetDistanceResponse;
import com.sap.co2calculator.service.CityService;
import com.sap.co2calculator.service.ConfigurationProvider;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CityServiceImpl implements CityService
{
    public static final String DISTANCE = "distance";
    public static final List<String> METRICS = Arrays.asList(DISTANCE);
    public static final String KM = "km";
    private final String getCityCoordinatesApiUrl;
    private final String getDistanceApiUrl;
    private static final Logger LOGGER = LogManager.getLogger(CityServiceImpl.class);

    private ConfigurationProvider configurationProvider;

    public CityServiceImpl(ConfigurationProvider configurationProvider)
    {
        this.configurationProvider = configurationProvider;
        getCityCoordinatesApiUrl = configurationProvider.getConfig("GET_CITY_COORDINATES_API_URL");
        getDistanceApiUrl = configurationProvider.getConfig("GET_DISTANCE_API_URL");
    }

    @Override public double distanceFromTo(String start, String end)
    {
        GetResponse startResponse = getCityCoordinates(start);
        GetResponse endResponse = getCityCoordinates(end);
        GetDistanceResponse distance =
                getDistance(startResponse.getFeatures().get(0).getGeometry().getCoordinates(),
                        endResponse.getFeatures().get(0).getGeometry().getCoordinates());
        return distance.getDistances().get(0).get(1);
    }

    private GetDistanceResponse getDistance(List<Double> from, List<Double> to)
    {
        ObjectMapper mapper = new ObjectMapper();
        GetDistanceResponse distanceResponse = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {

            GetDistanceRequest request = new GetDistanceRequest(Arrays.asList(from, to),
                    METRICS, KM);
            HttpEntity requestBody = new StringEntity(mapper.writeValueAsString(request), ContentType.APPLICATION_JSON);
            HttpUriRequest httpRequest = RequestBuilder.post(getDistanceApiUrl)
                    .setHeader("Authorization", getOrsToken())
                    .setEntity(requestBody).build();


            distanceResponse = client.execute(httpRequest, httpResponse -> {
                String responseContent = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                if( LOGGER.isInfoEnabled())
                    LOGGER.info(responseContent);
                return mapper.readValue(responseContent, GetDistanceResponse.class);
            });
        }
        catch (IOException e)
        {
            String error = String.format("an error during distance between %s and %s",
                    Arrays.toString(from.toArray()), Arrays.toString(to.toArray()));
            if( LOGGER.isErrorEnabled() )
                LOGGER.error(error, e);
            throw new RuntimeException(error, e);
        }
        return distanceResponse;
    }

    private GetResponse getCityCoordinates(String cityName)
    {
        ObjectMapper mapper = new ObjectMapper();
        GetResponse response = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpUriRequest httpRequest = RequestBuilder.get(getCityCoordinatesApiUrl)
                    .addParameter("text", cityName)
                    .addParameter("api_key", getOrsToken()).build();

            response = client.execute(httpRequest, httpResponse -> {
                String responseContent = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                return mapper.readValue(responseContent, GetResponse.class);
            });
        }
        catch (IOException e)
        {
            if( LOGGER.isErrorEnabled() )
                LOGGER.error("an error during getting city " + cityName, e);
            throw new RuntimeException("an error during getting city " + cityName, e);
        }
        return response;
    }

    private String getOrsToken(){
        return configurationProvider.getConfig("ORS_TOKEN");
    }
}
