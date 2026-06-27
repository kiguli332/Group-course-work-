package com.firstbank.uganda.model;

/**
 * Factory class for creating Account instances based on account type.
 * Demonstrates polymorphism by returning the appropriate concrete subclass.
 */
public class AccountFactory {

    public static Account createAccount(String accountType) {
        if (accountType == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }

        switch (accountType) {
            case "Savings":
                return new SavingsAccount();
            case "Current":
                return new CurrentAccount();
            case "Fixed Deposit":
                return new FixedDepositAccount();
            case "Student":
                return new StudentAccount();
            case "Joint":
                return new JointAccount();
            default:
                throw new IllegalArgumentException("Unknown account type: " + accountType);
        }
    }
}
