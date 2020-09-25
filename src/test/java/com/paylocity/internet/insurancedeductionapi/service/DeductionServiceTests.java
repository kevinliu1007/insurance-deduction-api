package com.paylocity.internet.insurancedeductionapi.service;

import com.paylocity.internet.insurancedeductionapi.model.DeductionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DeductionServiceTests {
    @Autowired
    DeductionService deductionService;

    @Test
    public void singleEmployeeNoDiscountTest() {
        DeductionResponse deductionResponse = deductionService.calculateDeduction("Kevin Liu", null);
        assertEquals(deductionResponse.getEmployeeDeduction().getEmployeeName(), "Kevin Liu");
        assertEquals(deductionResponse.getEmployeeDeduction().getCompensation(), 1961.54);
        assertEquals(deductionResponse.getEmployeeDeduction().getCompensationLastMonth(), 1961.5);
        assertEquals(deductionResponse.getEmployeeDeduction().isDiscountApplied(), false);
    }

    @Test
    public void singleEmployeeWithDiscountTest() {
        DeductionResponse deductionResponse = deductionService.calculateDeduction("Kevin Anqing Liu", null);
        assertEquals(deductionResponse.getEmployeeDeduction().getEmployeeName(), "Kevin Anqing Liu");
        assertEquals(deductionResponse.getEmployeeDeduction().getCompensation(), 1965.38);
        assertEquals(deductionResponse.getEmployeeDeduction().getCompensationLastMonth(), 1965.5);
        assertEquals(deductionResponse.getEmployeeDeduction().isDiscountApplied(), true);
    }

    @Test
    public void employeeWithOneDependent() {
        DeductionResponse deductionResponse = deductionService
                .calculateDeduction("Kevin Anqing Liu", "John Liu");
        assertEquals(deductionResponse.getDependentDeduction().get(0).getDependentName(), "John Liu");
        assertEquals(deductionResponse.getDependentDeduction().get(0).getPaycutPerPaycheck(), 19.23);
        assertEquals(deductionResponse.getDependentDeduction().get(0).getPaycutLastPaycheck(), 19.25);
        assertEquals(deductionResponse.getDependentDeduction().get(0).isDiscountApplied(), false);
    }

    @Test
    public void employeeWithOneDependentWithDiscount() {
        DeductionResponse deductionResponse = deductionService
                .calculateDeduction("Kevin Anqing Liu", "Angela Liu");
        assertEquals(deductionResponse.getDependentDeduction().get(0).getDependentName(), "Angela Liu");
        assertEquals(deductionResponse.getDependentDeduction().get(0).getPaycutPerPaycheck(), 17.31);
        assertEquals(deductionResponse.getDependentDeduction().get(0).getPaycutLastPaycheck(), 17.25);
        assertEquals(deductionResponse.getDependentDeduction().get(0).isDiscountApplied(), true);
    }
}
