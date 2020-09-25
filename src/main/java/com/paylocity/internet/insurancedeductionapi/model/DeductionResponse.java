package com.paylocity.internet.insurancedeductionapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeductionResponse extends RepresentationModel<DeductionResponse> {

    @JsonProperty
    @ApiModelProperty(notes = "Employee's compensation per paycheck")
    private double compensationPerPaycheck;

    @JsonProperty
    @ApiModelProperty(notes = "Discount percentage if applied")
    private String discountRate;

    @JsonProperty
    @ApiModelProperty(notes = "Employee's deduction details")
    private EmployeeDeduction employeeDeduction;

    @JsonProperty
    @ApiModelProperty(notes = "Employee's dependents' deduction details")
    private List<DependentDeduction> dependentDeduction;
}
