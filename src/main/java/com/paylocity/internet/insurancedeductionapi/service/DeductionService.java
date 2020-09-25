package com.paylocity.internet.insurancedeductionapi.service;

import com.paylocity.internet.insurancedeductionapi.model.DeductionRequest;
import com.paylocity.internet.insurancedeductionapi.model.DeductionResponse;
import com.paylocity.internet.insurancedeductionapi.model.DependentDeduction;
import com.paylocity.internet.insurancedeductionapi.model.EmployeeDeduction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DeductionService {

    private static double COMPENSATION_PER_PAYCHECK = 2000.0;

    private static double EMPLOYEE_YEARLY_CHARGE = 1000.0;

    private static double DEPENDENT_YEARLY_CHARGE = 500.0;

    private static double DISCOUNT_PERCENTAGE = 0.1;

    private static int PAYCHECKS_PER_YEAR = 26;

    private Logger logger = LoggerFactory.getLogger(DeductionService.class);

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
        double paycutPerMonth = 0.0;
        double charge = EMPLOYEE_YEARLY_CHARGE;

        if (!isEmployee) {
            charge = DEPENDENT_YEARLY_CHARGE;
        }

        if (discountApplied) {
            charge *= (1 - DISCOUNT_PERCENTAGE);
        }

        paycutPerMonth = Math.round(charge / PAYCHECKS_PER_YEAR * 100.0) / 100.0;

        return paycutPerMonth;
    }

    private double getPaycutLastMonth(double paycutPerMonth, boolean discountApplied, boolean isEmployee) {
        double paycutLastMonth = 0.0;
        double charge = EMPLOYEE_YEARLY_CHARGE;

        if (!isEmployee) {
            charge = DEPENDENT_YEARLY_CHARGE;
        }

        if (discountApplied) {
            charge *= (1 - DISCOUNT_PERCENTAGE);
        }

        paycutLastMonth = Math.round((charge - paycutPerMonth * (PAYCHECKS_PER_YEAR - 1)) * 100.0) / 100.0;

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
        if (deductionRequest.getDependentNames() == null || deductionRequest.getDependentNames().size() == 0) {
            return null;
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
                && deductionResponse.getDependentDeduction().size() > 0) {
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

        employeeDeduction.setCompensation(COMPENSATION_PER_PAYCHECK - totalDeduction);
        employeeDeduction.setCompensationLastMonth(COMPENSATION_PER_PAYCHECK - totalDeductionLastMonth);
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
        DeductionRequest deductionRequest = parseDeductionRequest(employee, dependent);
        logger.info("Starting deduction calculations.");
        DeductionResponse deductionResponse = new DeductionResponse();
        EmployeeDeduction employeeDeduction = calculateEmployeeDeduction(deductionRequest);
        List<DependentDeduction> dependentDeductions = calculateDependentDeduction(deductionRequest);

        deductionResponse.setEmployeeDeduction(employeeDeduction);
        deductionResponse.setDependentDeduction(dependentDeductions);
        deductionResponse.setDiscountRate(Math.round(DISCOUNT_PERCENTAGE * 100) + "%");
        deductionResponse.setCompensationPerPaycheck(COMPENSATION_PER_PAYCHECK);

        calculateCompensations(deductionResponse);
        logger.info("Deduction calculation completed successfully.");

        return deductionResponse;
    }
}
