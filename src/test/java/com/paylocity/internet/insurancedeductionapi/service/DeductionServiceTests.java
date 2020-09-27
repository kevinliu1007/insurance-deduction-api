package com.paylocity.internet.insurancedeductionapi.service;

import com.paylocity.internet.insurancedeductionapi.model.DeductionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DeductionServiceTests {
    @Autowired
    DeductionService deductionService;

    @Test
    void singleEmployeeNoDiscountTest() {
        DeductionResponse deductionResponse = deductionService.calculateDeduction("Kevin Liu", null);
        assertEquals("Kevin Liu", deductionResponse.getEmployeeDeduction().getEmployeeName());
        assertEquals(1961.54, deductionResponse.getEmployeeDeduction().getCompensation());
        assertEquals(1961.5, deductionResponse.getEmployeeDeduction().getCompensationLastMonth());
        assertEquals(false, deductionResponse.getEmployeeDeduction().isDiscountApplied());
    }

    @Test
    void singleEmployeeWithDiscountTest() {
        DeductionResponse deductionResponse = deductionService.calculateDeduction("Kevin Anqing Liu", null);
        assertEquals("Kevin Anqing Liu", deductionResponse.getEmployeeDeduction().getEmployeeName());
        assertEquals(1965.38, deductionResponse.getEmployeeDeduction().getCompensation());
        assertEquals(1965.5,deductionResponse.getEmployeeDeduction().getCompensationLastMonth());
        assertEquals(true, deductionResponse.getEmployeeDeduction().isDiscountApplied());
    }

    @Test
    void employeeWithOneDependent() {
        DeductionResponse deductionResponse = deductionService
                .calculateDeduction("Kevin Anqing Liu", "John Liu");
        assertEquals("John Liu", deductionResponse.getDependentDeduction().get(0).getDependentName());
        assertEquals(19.23, deductionResponse.getDependentDeduction().get(0).getPaycutPerPaycheck());
        assertEquals(19.25, deductionResponse.getDependentDeduction().get(0).getPaycutLastPaycheck());
        assertEquals(false, deductionResponse.getDependentDeduction().get(0).isDiscountApplied());
    }

    @Test
    void employeeWithOneDependentWithDiscount() {
        DeductionResponse deductionResponse = deductionService
                .calculateDeduction("Kevin Anqing Liu", "Angela Liu");
        assertEquals("Angela Liu", deductionResponse.getDependentDeduction().get(0).getDependentName());
        assertEquals(17.31, deductionResponse.getDependentDeduction().get(0).getPaycutPerPaycheck());
        assertEquals(17.25, deductionResponse.getDependentDeduction().get(0).getPaycutLastPaycheck());
        assertEquals(true, deductionResponse.getDependentDeduction().get(0).isDiscountApplied());
    }
}
