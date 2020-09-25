package com.paylocity.internet.insurancedeductionapi.controller;

import com.paylocity.internet.insurancedeductionapi.model.DeductionResponse;
import com.paylocity.internet.insurancedeductionapi.service.DeductionService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;

@Api(protocols = "http", value = "/paylocity/insurance")
@RestController
@RequestMapping("/paylocity/insurance")
public class InsuranceDeductionController {

    @Autowired
    DeductionService deductionService;

    Logger logger = LoggerFactory.getLogger(InsuranceDeductionController.class);

    @ApiOperation("Get request to calculate the employee's income deduction from health insurance. Values change " +
            "base on the number of dependents under the employee as well.")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Business rule error."),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "User authentication failed."),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Not authorized to access resource."),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Service invocation error.")
    })
    @GetMapping(value = "/deduction")
    @ResponseStatus(value = HttpStatus.OK)
    public DeductionResponse calculateDeduction(@RequestParam String employee,
                                                @RequestParam(required = false) String dependent) {
        long start = System.currentTimeMillis();
        DeductionResponse response = deductionService.calculateDeduction(employee, dependent);
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(InsuranceDeductionController.class)
                        .calculateDeduction(employee, dependent))
                .withSelfRel();
        response.add(selfLink);
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        logger.info("Executed in time: {}ms", timeElapsed);

        return response;
    }
}
