package com.sap.co2calculator.openrouteservice.pojo.getdistance.request;

import java.util.List;

public class GetDistanceRequest
{
    private List<List<Double>> locations;
    private List<String> metrics;
    private String units;

    public GetDistanceRequest(List<List<Double>> locations, List<String> metrics, String units)
    {
        this.locations = locations;
        this.metrics = metrics;
        this.units = units;
    }

    public List<List<Double>> getLocations()
    {
        return locations;
    }

    public List<String> getMetrics()
    {
        return metrics;
    }

    public String getUnits()
    {
        return units;
    }
}
