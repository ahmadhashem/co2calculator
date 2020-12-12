package com.sap.co2calculator.service.impl;

import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class TransportationCo2EquivalentCalculatorImplTest
{
    @Test
    public void testCalculation()
    {
        TransportationCo2EquivalentCalculatorImpl calculator = new TransportationCo2EquivalentCalculatorImpl(
                transportationType -> "car".equals(transportationType)?6:5);
        double car = calculator.calculate(200.351564, "car");
        double other =calculator.calculate(413.72945, "other");
        assertThat(car, closeTo(1202.1, 0.1));
        assertThat(other, closeTo(2068.6, 0.1));

    }
}