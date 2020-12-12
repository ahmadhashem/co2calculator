package com.sap.co2calculator;

import com.sap.co2calculator.service.CityService;
import com.sap.co2calculator.service.ConfigurationProvider;
import com.sap.co2calculator.service.TransportationCo2EquivalentCalculator;
import com.sap.co2calculator.service.TransportationCo2EquivalentProvider;

public interface ApplicationContext
{
    CityService cityService();
    ConfigurationProvider configurationProvider();
    TransportationCo2EquivalentProvider transportationCo2EquivalentProvider();
    TransportationCo2EquivalentCalculator transportationCo2EquivalentCalculator();
}
