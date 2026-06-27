package com.firstbank.uganda.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Abstract base class for all account types at First Bank Uganda.
 * Defines common state and enforces polymorphic behavior for account-specific rules.
 * 
 * @author Development Team
 * @version 1.0
 */
public abstract class Account {

    protected String accountNumber;
    protected String firstName;
    protected String lastName;
    protected String nin; // National Identification Number
    protected String email;
    protected String phoneNumber;
    protected String pin;
    protected LocalDate dateOfBirth;
    protected String branchCode;
    protected String branchName;
    protected BigDecimal openingDeposit;
    protected LocalDate dateOpened;
    protected String jointNin; // Only used for Joint accounts

    public Account() {
        this.dateOpened = LocalDate.now();
    }

    /**
     * Each account type must define its minimum opening deposit.
     * @return minimum deposit as BigDecimal
     */
    public abstract BigDecimal getMinimumDeposit();

    /**
     * Returns a description of special rules for this account type.
     */
    public abstract String getSpecialRules();

    /**
     * Returns the account type identifier string.
     */
    public abstract String getAccountType();

    /**
     * Validates if the applicant's age meets account-specific requirements.
     */
    public abstract boolean isAgeValid(int age);

    // Getters and Setters
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getNin() { return nin; }
    public void setNin(String nin) { this.nin = nin; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public BigDecimal getOpeningDeposit() { return openingDeposit; }
    public void setOpeningDeposit(BigDecimal openingDeposit) { this.openingDeposit = openingDeposit; }

    public LocalDate getDateOpened() { return dateOpened; }

    public String getJointNin() { return jointNin; }
    public void setJointNin(String jointNin) { this.jointNin = jointNin; }

    /**
     * Calculates age from date of birth.
     */
    public int getAge() {
        if (dateOfBirth == null) return 0;
        return LocalDate.now().getYear() - dateOfBirth.getYear() - 
               (LocalDate.now().getDayOfYear() < dateOfBirth.getDayOfYear() ? 1 : 0);
    }

    /**
     * Formats the account record for display.
     */
    public String getFormattedRecord() {
        return String.format("ACC: %s | %s %s | %s | %s | DOB %s | %s | Deposit %,d | %s",
            accountNumber,
            lastName, firstName,
            getAccountType(),
            branchName,
            dateOfBirth.toString(),
            phoneNumber,
            openingDeposit.longValue(),
            email
        );
    }

    @Override
    public String toString() {
        return getFormattedRecord();
    }
}
