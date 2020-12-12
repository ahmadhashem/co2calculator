package com.sap.co2calculator;

import picocli.CommandLine;

public class MainEntryPoint
{
    public static void main(String [] args){
        // instead of depending on spring-core, spring-beans, spring-context, building simple context
        // that demos dependency injection
        ApplicationContext applicationContext = new DefaultApplicationContext();

        int exitCode = new CommandLine(new CalculateCo2Command(applicationContext)).execute(args);
        System.exit(exitCode);
    }
}
