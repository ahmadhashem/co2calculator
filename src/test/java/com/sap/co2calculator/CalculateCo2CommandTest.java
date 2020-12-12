package com.sap.co2calculator;

import com.sap.co2calculator.service.CityService;
import com.sap.co2calculator.service.ConfigurationProvider;
import com.sap.co2calculator.service.TransportationCo2EquivalentCalculator;
import com.sap.co2calculator.service.TransportationCo2EquivalentProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CalculateCo2CommandTest
{
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
    private CalculateCo2Command calculateCo2Command;

    @Before
    public void setup()
    {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
        calculateCo2Command = new CalculateCo2Command(new ApplicationContext()
        {
            @Override public CityService cityService()
            {
                return mock(CityService.class);
            }

            @Override public ConfigurationProvider configurationProvider()
            {
                return mock(ConfigurationProvider.class);
            }

            @Override
            public TransportationCo2EquivalentProvider transportationCo2EquivalentProvider()
            {
                return mock(TransportationCo2EquivalentProvider.class);
            }

            @Override
            public TransportationCo2EquivalentCalculator transportationCo2EquivalentCalculator()
            {
                TransportationCo2EquivalentCalculator calculator =
                        mock(TransportationCo2EquivalentCalculator.class);
                when(calculator.calculate(anyDouble(), contains("car"))).thenReturn(1234.5678);
                when(calculator.calculate(anyDouble(), contains("invalid")))
                        .thenThrow(new RuntimeException("invalid transportation method"));
                when(calculator.calculate(anyDouble(), contains("api-error")))
                        .thenThrow(new RuntimeException("Api error : token expired", new IOException("token expired")));
                return calculator;
            }
        });
    }

    @Test
    public void testWithRequiredParams()
    {
        int exitCode = new CommandLine(calculateCo2Command).
                execute("--start=Cairo", "--end=Alexandria", "--transportation-method=car");
        assertThat(exitCode, equalTo(0));
        assertThat(outputStreamCaptor.toString(),
                equalTo("Your trip caused 1.23kg of CO2-equivalent.\n"));
    }

    @Test
    public void testWithRequiredParamsWithoutEquals()
    {
        int exitCode = new CommandLine(calculateCo2Command)
                .execute("--start", "Cairo", "--end", "Alexandria", "--transportation-method","car");
        assertThat(exitCode, equalTo(0));
        assertThat(outputStreamCaptor.toString(),
                equalTo("Your trip caused 1.23kg of CO2-equivalent.\n"));
    }

    @Test
    public void testWithMissingStart()
    {
        String expectedOut = "Expected parameter for option '--start' but found '--end'\n"
                + "Usage: co2calculator [-hV] --end=<end> --start=<start>\n"
                + "                     --transportation-method=<transportation>\n"
                + "      --end=<end>\n"
                + "  -h, --help            Show this help message and exit.\n"
                + "      --start=<start>\n"
                + "      --transportation-method=<transportation>\n"
                + "\n"
                + "  -V, --version         Print version information and exit.\n";
        int exitCode = new CommandLine(calculateCo2Command)
                .execute("--start", "--end", "Alexandria", "--transportation-method","car");
        assertThat(exitCode, equalTo(2));
        assertThat(errorStreamCaptor.toString(), equalTo(expectedOut));
    }

    @Test
    public void testWithMissingEnd()
    {
        String expectedOut = "Expected parameter for option '--end' but found '--transportation-method'\n"
                + "Usage: co2calculator [-hV] --end=<end> --start=<start>\n"
                + "                     --transportation-method=<transportation>\n"
                + "      --end=<end>\n"
                + "  -h, --help            Show this help message and exit.\n"
                + "      --start=<start>\n"
                + "      --transportation-method=<transportation>\n"
                + "\n"
                + "  -V, --version         Print version information and exit.\n";
        int exitCode = new CommandLine(calculateCo2Command)
                .execute("--start", "Cairo", "--end", "--transportation-method","car");
        assertThat(exitCode, equalTo(2));
        assertThat(errorStreamCaptor.toString(), equalTo(expectedOut));
    }

    @Test
    public void testWithMissingTransportationMethod()
    {
        String expectedOut = "Missing required option: '--transportation-method=<transportation>'\n"
                + "Usage: co2calculator [-hV] --end=<end> --start=<start>\n"
                + "                     --transportation-method=<transportation>\n"
                + "      --end=<end>\n"
                + "  -h, --help            Show this help message and exit.\n"
                + "      --start=<start>\n"
                + "      --transportation-method=<transportation>\n"
                + "\n"
                + "  -V, --version         Print version information and exit.\n";
        int exitCode = new CommandLine(calculateCo2Command)
                .execute("--start", "Cairo", "--end", "Alexandria");
        assertThat(exitCode, equalTo(2));
        assertThat(errorStreamCaptor.toString(), equalTo(expectedOut));
    }

    @Test
    public void testWithRequiredParamsWithInvalidTransportation()
    {
        int exitCode = new CommandLine(calculateCo2Command)
                .execute("--start", "Cairo", "--end", "Alexandria", "--transportation-method","invalid");
        assertThat(exitCode, equalTo(1));
        assertThat(outputStreamCaptor.toString(),
                equalTo("Error during communicating with external api because of invalid transportation method\n"));
    }

    @Test
    public void testWithApiError()
    {
        int exitCode = new CommandLine(calculateCo2Command)
                .execute("--start", "Cairo", "--end", "Alexandria", "--transportation-method","api-error");
        assertThat(exitCode, equalTo(1));
        assertThat(outputStreamCaptor.toString(),
                equalTo("Error during communicating with external api because of Api error : token expired\n"));
    }
}