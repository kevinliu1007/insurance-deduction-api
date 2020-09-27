package com.paylocity.internet.insurancedeductionapi.service;

import com.paylocity.internet.insurancedeductionapi.model.*;
import com.paylocity.internet.insurancedeductionapi.repository.PayDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DeductionService {

    @Autowired
    PayDataRepository payDataRepository;

    private Double compensationPerPaycheck;

    private Double employeeYearlyCharge;

    private Double dependentYearlyCharge;

    private Double discountPercentage;

    private Integer paychecksPerYear;

    private Logger logger = LoggerFactory.getLogger(DeductionService.class);

    private void setUpData() {
        if (compensationPerPaycheck == null || compensationPerPaycheck.isNaN()) {
           compensationPerPaycheck = payDataRepository.findByPayData("paycheck_amount").get(0).getValue();
           logger.info("Query for paycheck amount data.");
        }

        if (employeeYearlyCharge == null || employeeYearlyCharge.isNaN()) {
            employeeYearlyCharge = payDataRepository.findByPayData("employee_yearly_charge").get(0).getValue();
            logger.info("Query for employee yearly charge data.");
        }

        if (dependentYearlyCharge == null || dependentYearlyCharge.isNaN()) {
            dependentYearlyCharge = payDataRepository.findByPayData("dependent_yearly_charge").get(0).getValue();
            logger.info("Query for dependent yearly charge data.");
        }

        if (discountPercentage == null || discountPercentage.isNaN()) {
            discountPercentage = payDataRepository.findByPayData("discount_rate").get(0).getValue();
            logger.info("Query for discount rate data.");
        }

        if (paychecksPerYear == null) {
            paychecksPerYear = payDataRepository.findByPayData("paychecks_per_year").get(0).getValue().intValue();
            logger.info("Query for paychecks per year data.");
        }
    }

    private boolean checkDiscount(String name) {
        String[] names = name.split("\\s+");

        for (String n : names) {
            if (!n.isEmpty() && (n.charAt(0) == 'a' || n.charAt(0) == 'A')) {
                return true;
            }
        }

        return false;
    }

    private double getPaycutPerMonth(boolean discountApplied, boolean isEmployee) {
        double paycutPerMonth;
        double charge = employeeYearlyCharge;

        if (!isEmployee) {
            charge = dependentYearlyCharge;
        }

        if (discountApplied) {
            charge *= (1 - discountPercentage);
        }

        paycutPerMonth = Math.round(charge / paychecksPerYear * 100.0) / 100.0;

        return paycutPerMonth;
    }

    private double getPaycutLastMonth(double paycutPerMonth, boolean discountApplied, boolean isEmployee) {
        double paycutLastMonth;
        double charge = employeeYearlyCharge;

        if (!isEmployee) {
            charge = dependentYearlyCharge;
        }

        if (discountApplied) {
            charge *= (1 - discountPercentage);
        }

        paycutLastMonth = Math.round((charge - paycutPerMonth * (paychecksPerYear - 1)) * 100.0) / 100.0;

        return paycutLastMonth;
    }

    private EmployeeDeduction calculateEmployeeDeduction(DeductionRequest deductionRequest) {
        EmployeeDeduction employeeDeduction = new EmployeeDeduction();
        employeeDeduction.setEmployeeName(deductionRequest.getEmployeeName());
        employeeDeduction.setDiscountApplied(checkDiscount(deductionRequest.getEmployeeName()));
        employeeDeduction.setPaycutPerPaycheck(getPaycutPerMonth(employeeDeduction.isDiscountApplied(), true));
        employeeDeduction.setPaycutLastPaycheck(getPaycutLastMonth(employeeDeduction.getPaycutPerPaycheck(),
                employeeDeduction.isDiscountApplied(), true));

        return employeeDeduction;
    }

    private List<DependentDeduction> calculateDependentDeduction(DeductionRequest deductionRequest) {
        if (deductionRequest.getDependentNames() == null || deductionRequest.getDependentNames().isEmpty()) {
            return new ArrayList<>();
        }

        List<DependentDeduction> dependentDeductions = new ArrayList<>();

        for (String name : deductionRequest.getDependentNames()) {
            DependentDeduction dependentDeduction = new DependentDeduction();
            dependentDeduction.setDependentName(name);
            dependentDeduction.setDiscountApplied(checkDiscount(name));
            dependentDeduction.setPaycutPerPaycheck(getPaycutPerMonth(dependentDeduction.isDiscountApplied(), false));
            dependentDeduction.setPaycutLastPaycheck(getPaycutLastMonth(dependentDeduction.getPaycutPerPaycheck(),
                    dependentDeduction.isDiscountApplied(), false));
            dependentDeductions.add(dependentDeduction);
        }

        return dependentDeductions;
    }

    private void calculateCompensations(DeductionResponse deductionResponse) {
        EmployeeDeduction employeeDeduction = deductionResponse.getEmployeeDeduction();
        double totalDeduction = 0.0;
        double totalDeductionLastMonth = 0.0;

        if (deductionResponse.getDependentDeduction() != null
                && !deductionResponse.getDependentDeduction().isEmpty()) {
            List<DependentDeduction> dependentDeductions = deductionResponse.getDependentDeduction();
            double totalDependentsDeduction = 0.0;
            double totalDependentsDeductionLastMonth = 0.0;

            for (DependentDeduction dependentDeduction : dependentDeductions) {
                totalDependentsDeduction += dependentDeduction.getPaycutPerPaycheck();
                totalDependentsDeductionLastMonth += dependentDeduction.getPaycutPerPaycheck();
            }

            totalDeduction += totalDependentsDeduction;
            totalDeductionLastMonth += totalDependentsDeductionLastMonth;
        }

        totalDeduction += deductionResponse.getEmployeeDeduction().getPaycutPerPaycheck();
        totalDeductionLastMonth += deductionResponse.getEmployeeDeduction().getPaycutLastPaycheck();

        employeeDeduction.setCompensation(compensationPerPaycheck - totalDeduction);
        employeeDeduction.setCompensationLastMonth(compensationPerPaycheck - totalDeductionLastMonth);
    }

    private DeductionRequest parseDeductionRequest(String employee, String dependent) {
        DeductionRequest deductionRequest = new DeductionRequest();
        deductionRequest.setEmployeeName(employee);
        if (dependent == null || dependent.isEmpty()) {
            deductionRequest.setDependentNames(null);
        } else {
            deductionRequest.setDependentNames(Arrays.asList(dependent.split(",")));
        }

        return deductionRequest;
    }

    public DeductionResponse calculateDeduction(String employee, String dependent) {
        setUpData();

        DeductionRequest deductionRequest = parseDeductionRequest(employee, dependent);
        logger.info("Starting deduction calculations.");
        DeductionResponse deductionResponse = new DeductionResponse();
        EmployeeDeduction employeeDeduction = calculateEmployeeDeduction(deductionRequest);
        List<DependentDeduction> dependentDeductions = calculateDependentDeduction(deductionRequest);

        deductionResponse.setEmployeeDeduction(employeeDeduction);
        deductionResponse.setDependentDeduction(dependentDeductions);
        deductionResponse.setDiscountRate(Math.round(discountPercentage * 100) + "%");
        deductionResponse.setCompensationPerPaycheck(compensationPerPaycheck);

        calculateCompensations(deductionResponse);
        logger.info("Deduction calculation completed successfully.");

        return deductionResponse;
    }
}
