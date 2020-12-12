package com.sap.co2calculator.exception;

public class InvalidTransportationTypeException
        extends RuntimeException
{
    public InvalidTransportationTypeException(String message)
    {
        super(message);
    }
}
