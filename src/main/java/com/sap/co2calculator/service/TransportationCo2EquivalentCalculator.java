package com.sap.co2calculator.service;

import com.sap.co2calculator.exception.InvalidTransportationTypeException;

public interface TransportationCo2EquivalentCalculator
{
    double calculate(double distance, String transportType)
            throws InvalidTransportationTypeException;
}
