package com.sap.co2calculator.service.impl;

import com.sap.co2calculator.exception.ConfigurationException;
import com.sap.co2calculator.service.ConfigurationProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigurationProviderImpl
        implements ConfigurationProvider
{
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationProviderImpl.class);
    private final Map<String, String> config = new ConcurrentHashMap();
    public final String tokenVarName;

    public ConfigurationProviderImpl(String configFilePath, String tokenVarName) throws ConfigurationException
    {
        this.tokenVarName = tokenVarName;
        Properties properties = new Properties();
        try
        {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFilePath);
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

        for(String propName : properties.stringPropertyNames())
            config.put(propName, properties.getProperty(propName));

        if( !System.getenv().containsKey(tokenVarName)  || System.getenv(tokenVarName).length() == 0)
            throw new ConfigurationException("ORS_TOKEN is missing!");

        config.put(tokenVarName, System.getenv(tokenVarName));

    }

    @Override public String getConfig(String name)
    {
        return config.get(name);
    }
}
