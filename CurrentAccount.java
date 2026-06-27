package com.firstbank.uganda.model;

import java.math.BigDecimal;

/**
 * Current Account - Overdraft allowed, no interest earned.
 * Minimum opening deposit: UGX 200,000
 */
public class CurrentAccount extends Account {

    private static final BigDecimal MINIMUM_DEPOSIT = new BigDecimal("200000");
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 75;

    @Override
    public BigDecimal getMinimumDeposit() {
        return MINIMUM_DEPOSIT;
    }

    @Override
    public String getSpecialRules() {
        return "Overdraft allowed, no interest";
    }

    @Override
    public String getAccountType() {
        return "Current";
    }

    @Override
    public boolean isAgeValid(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }
}
