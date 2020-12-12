
package com.sap.co2calculator.openrouteservice.pojo.getcoordinates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "features",
})
public class GetResponse
{

    @JsonProperty("features")
    private List<Feature> features = null;
    @JsonProperty("features")
    public List<Feature> getFeatures() {
        return features;
    }

    @JsonProperty("features")
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }
}
