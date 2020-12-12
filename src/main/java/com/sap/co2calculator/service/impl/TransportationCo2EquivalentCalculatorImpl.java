package com.sap.co2calculator.service.impl;

import com.sap.co2calculator.exception.InvalidTransportationTypeException;
import com.sap.co2calculator.service.TransportationCo2EquivalentCalculator;
import com.sap.co2calculator.service.TransportationCo2EquivalentProvider;

public class TransportationCo2EquivalentCalculatorImpl
        implements TransportationCo2EquivalentCalculator
{
    private final TransportationCo2EquivalentProvider co2EquivalentProvider;

    public TransportationCo2EquivalentCalculatorImpl(TransportationCo2EquivalentProvider co2EquivalentProvider)
    {
        this.co2EquivalentProvider = co2EquivalentProvider;
    }

    @Override public double calculate(double distance, String transportType)
            throws InvalidTransportationTypeException
    {
        int co2 = co2EquivalentProvider.getCo2Equivalent(transportType);
        return distance*co2;
    }
}
