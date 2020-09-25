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
public class EmployeeDeduction {

    @JsonProperty
    @ApiModelProperty(notes = "Employee's name")
    private String employeeName;

    @JsonProperty
    @ApiModelProperty(notes = "Employee's net paycheck after deduction excluding last paycheck")
    private double compensation;

    @JsonProperty
    @ApiModelProperty(notes = "Employee's net paycheck for the last paycheck")
    private double compensationLastMonth;

    @JsonProperty
    @ApiModelProperty(notes = "Employee's pay cut per paycheck excluding last paycheck")
    private double paycutPerPaycheck;

    @JsonProperty
    @ApiModelProperty(notes = "Employee's pay cut for last paycheck")
    private double paycutLastPaycheck;

    @JsonProperty
    @ApiModelProperty(notes = "If discount is applied for employee or not")
    private boolean discountApplied;
}
