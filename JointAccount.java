package com.firstbank.uganda.model;

import java.math.BigDecimal;

/**
 * Joint Account - Shared account requiring a second NIN.
 * Minimum opening deposit: UGX 100,000
 */
public class JointAccount extends Account {

    private static final BigDecimal MINIMUM_DEPOSIT = new BigDecimal("100000");
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 75;

    @Override
    public BigDecimal getMinimumDeposit() {
        return MINIMUM_DEPOSIT;
    }

    @Override
    public String getSpecialRules() {
        return "Requires a second NIN";
    }

    @Override
    public String getAccountType() {
        return "Joint";
    }

    @Override
    public boolean isAgeValid(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }
}
