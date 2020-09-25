package com.paylocity.internet.insurancedeductionapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DependentDeduction {

    @JsonProperty
    @ApiModelProperty(notes = "Dependent's name")
    private String dependentName;

    @JsonProperty
    @ApiModelProperty(notes = "Dependent's pay cut per paycheck excluding last paycheck")
    private double paycutPerPaycheck;

    @JsonProperty
    @ApiModelProperty(notes = "Dependent's pay cut for the last paycheck")
    private double paycutLastPaycheck;

    @JsonProperty
    @ApiModelProperty(notes = "If discount is applied for the dependent or not")
    private boolean discountApplied;
}
