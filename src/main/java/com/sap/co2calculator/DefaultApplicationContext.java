package com.sap.co2calculator;

import com.sap.co2calculator.service.CityService;
import com.sap.co2calculator.service.ConfigurationProvider;
import com.sap.co2calculator.service.TransportationCo2EquivalentCalculator;
import com.sap.co2calculator.service.TransportationCo2EquivalentProvider;
import com.sap.co2calculator.service.impl.CityServiceImpl;
import com.sap.co2calculator.service.impl.ConfigurationProviderImpl;
import com.sap.co2calculator.service.impl.TransportationCo2EquivalentCalculatorImpl;
import com.sap.co2calculator.service.impl.TransportationCo2EquivalentProviderImpl;

public class DefaultApplicationContext implements ApplicationContext
{
    @Override public CityService cityService()
    {
        return new CityServiceImpl(configurationProvider());
    }

    @Override public ConfigurationProvider configurationProvider()
    {
        return new ConfigurationProviderImpl("config.properties", "ORS_TOKEN");
    }

    @Override public TransportationCo2EquivalentProvider transportationCo2EquivalentProvider()
    {
        return new TransportationCo2EquivalentProviderImpl("transportation-co2.properties");
    }

    @Override public TransportationCo2EquivalentCalculator transportationCo2EquivalentCalculator()
    {
        return new TransportationCo2EquivalentCalculatorImpl(transportationCo2EquivalentProvider());
    }
}
