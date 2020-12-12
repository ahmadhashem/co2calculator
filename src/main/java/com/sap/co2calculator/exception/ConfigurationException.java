package com.sap.co2calculator.exception;

public class ConfigurationException extends RuntimeException
{
    public ConfigurationException(String message, Throwable cause){
        super(cause.getMessage(), cause);
    }

    public ConfigurationException(String message)
    {
        super(message);
    }
}
