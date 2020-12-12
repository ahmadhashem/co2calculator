package com.sap.co2calculator;

import com.sap.co2calculator.service.CityService;
import com.sap.co2calculator.service.TransportationCo2EquivalentCalculator;
import com.sap.co2calculator.service.TransportationCo2EquivalentProvider;
import picocli.CommandLine;

@CommandLine.Command(name = "co2calculator", mixinStandardHelpOptions = true, version = "co2 calculator 0.0.1")
public class CalculateCo2Command implements Runnable
{
    private final CityService cityService;
    private final TransportationCo2EquivalentProvider co2EquivalentProvider;
    private final TransportationCo2EquivalentCalculator co2EquivalentCalculator;
    
    @CommandLine.Option(names="--start", required = true)
    private String start;

    @CommandLine.Option(names="--end", required = true)
    private String end;

    @CommandLine.Option(names="--transportation-method", required = true)
    private String transportation;

    private static final String OUTPUT = "Your trip caused %.2fkg of CO2-equivalent.";

    public CalculateCo2Command(ApplicationContext applicationContext)
    {
        this.cityService = applicationContext.cityService();
        this.co2EquivalentProvider = applicationContext.transportationCo2EquivalentProvider();
        this.co2EquivalentCalculator = applicationContext.transportationCo2EquivalentCalculator();
    }

    public void run() {
        try
        {
            double distance = cityService.distanceFromTo(start, end);
            double totalCo2InGrams = co2EquivalentCalculator.calculate(distance, transportation);
            System.out.println(String.format(OUTPUT, totalCo2InGrams / 1000.00));
        }
        catch (RuntimeException e)
        {
            System.out.println("Error during communicating with external api because of "+ e.getMessage());
            throw e;
        }
    }
}
