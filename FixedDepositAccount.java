package com.firstbank.uganda.model;

import java.math.BigDecimal;

/**
 * Fixed Deposit Account - Locked term, highest interest rate.
 * Minimum opening deposit: UGX 1,000,000
 */
public class FixedDepositAccount extends Account {

    private static final BigDecimal MINIMUM_DEPOSIT = new BigDecimal("1000000");
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 75;

    @Override
    public BigDecimal getMinimumDeposit() {
        return MINIMUM_DEPOSIT;
    }

    @Override
    public String getSpecialRules() {
        return "Locked term, highest interest";
    }

    @Override
    public String getAccountType() {
        return "Fixed Deposit";
    }

    @Override
    public boolean isAgeValid(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }
}
