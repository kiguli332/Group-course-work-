package com.firstbank.uganda.model;

import java.math.BigDecimal;

/**
 * Savings Account - Earns interest, no overdraft facility.
 * Minimum opening deposit: UGX 50,000
 */
public class SavingsAccount extends Account {

    private static final BigDecimal MINIMUM_DEPOSIT = new BigDecimal("50000");
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 75;

    @Override
    public BigDecimal getMinimumDeposit() {
        return MINIMUM_DEPOSIT;
    }

    @Override
    public String getSpecialRules() {
        return "Earns interest, no overdraft";
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }

    @Override
    public boolean isAgeValid(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }
}
