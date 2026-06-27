package com.firstbank.uganda.model;

import java.math.BigDecimal;

/**
 * Student Account - Special account for students aged 18-25.
 * Minimum opening deposit: UGX 10,000
 */
public class StudentAccount extends Account {

    private static final BigDecimal MINIMUM_DEPOSIT = new BigDecimal("10000");
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 25;

    @Override
    public BigDecimal getMinimumDeposit() {
        return MINIMUM_DEPOSIT;
    }

    @Override
    public String getSpecialRules() {
        return "Student account, reduced fees";
    }

    @Override
    public String getAccountType() {
        return "Student";
    }

    @Override
    public boolean isAgeValid(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }
}
