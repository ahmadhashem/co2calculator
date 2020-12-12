package com.sap.co2calculator.service.impl;

import com.sap.co2calculator.exception.ConfigurationException;
import com.sap.co2calculator.exception.InvalidTransportationTypeException;
import com.sap.co2calculator.service.TransportationCo2EquivalentProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is for the purpose of the task, better to store this in a global configuration service like consul
 */
public class TransportationCo2EquivalentProviderImpl implements TransportationCo2EquivalentProvider
{
    private final Properties properties;
    private static final Logger LOGGER = LogManager.getLogger(TransportationCo2EquivalentProviderImpl.class);

    public TransportationCo2EquivalentProviderImpl(String transportationCo2FilePath) throws ConfigurationException
    {
        properties = new Properties();
        try
        {
            InputStream inputStream =
                    getClass().getClassLoader().getResourceAsStream(transportationCo2FilePath);
            if( inputStream == null )
                throw new ConfigurationException("Can't read configuration, failing over earlier");
            properties.load(inputStream);
        }
        catch (IOException e)
        {
            if( LOGGER.isErrorEnabled())
                LOGGER.error("Can't read configuration, failing over earlier");
            throw new ConfigurationException("Can't read configuration, failing over earlier", e);
        }
    }

    @Override public int getCo2Equivalent(String transportationType) throws
            InvalidTransportationTypeException
    {
        if( !properties.containsKey(transportationType) )
            throw new InvalidTransportationTypeException(transportationType + " is not a valid transportation type");
        return Integer.parseInt(properties.getProperty(transportationType));
    }
}
