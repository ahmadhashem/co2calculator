package com.sap.co2calculator.service.impl;

import com.sap.co2calculator.exception.ConfigurationException;
import com.sap.co2calculator.exception.InvalidTransportationTypeException;
import org.junit.Test;


import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class TransportationCo2EquivalentProviderImplTest
{
    @Test
    public void testCanReadFile()
    {
        TransportationCo2EquivalentProviderImpl provider =
                new TransportationCo2EquivalentProviderImpl("dummy-co2.properties");
        assertThat(provider.getCo2Equivalent("dummy"), is(789));
    }

    @Test(expected = InvalidTransportationTypeException.class)
    public void testReadInvalidKey()
    {
        TransportationCo2EquivalentProviderImpl provider = new TransportationCo2EquivalentProviderImpl("dummy-co2.properties");
        provider.getCo2Equivalent("invalidKey");
    }

    @Test(expected = ConfigurationException.class)
    public void testInvalidConfigFile()
    {
        new TransportationCo2EquivalentProviderImpl("not-found.properties");
    }
}