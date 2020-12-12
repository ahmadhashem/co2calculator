package com.sap.co2calculator.service;

import com.sap.co2calculator.exception.InvalidTransportationTypeException;

public interface TransportationCo2EquivalentProvider
{
    // assume int would fit the co2 value, assume no fractions
    int getCo2Equivalent(String transportationType) throws InvalidTransportationTypeException;
}
