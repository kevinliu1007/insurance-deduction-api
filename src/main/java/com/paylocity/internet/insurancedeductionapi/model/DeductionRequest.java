package com.paylocity.internet.insurancedeductionapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeductionRequest {

    @JsonProperty("employeeName")
    private String employeeName;

    @JsonProperty("dependentNames")
    private List<String> dependentNames;
}
