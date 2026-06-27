package com.firstbank.uganda.validation;

import javafx.scene.control.*;
import java.util.*;
import java.util.regex.Pattern;
import com.firstbank.uganda.model.Account;

/**
 * Comprehensive validation utility for the account opening form.
 * Provides inline error messages and summary validation.
 */
public class Validator {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]{2,30}$");
    private static final Pattern NIN_PATTERN = Pattern.compile("^[A-Z0-9]{14}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+256[0-9]{9}$");
    private static final Pattern PIN_PATTERN = Pattern.compile("^[0-9]{4,6}$");

    private final List<ValidationResult> results = new ArrayList<>();
    private final Map<Control, Label> errorLabels = new HashMap<>();

    /**
     * Registers an error label for a control.
     */
    public void registerErrorLabel(Control control, Label errorLabel) {
        errorLabels.put(control, errorLabel);
    }

    /**
     * Validates a text field with a custom rule.
     */
    public boolean validateTextField(TextField field, String fieldName, 
                                     java.util.function.Function<String, String> rule) {
        String value = field.getText() != null ? field.getText().trim() : "";
        String error = rule.apply(value);

        boolean isValid = error == null;
        results.add(new ValidationResult(isValid, error, field));

        if (!isValid) {
            showError(field, error);
        } else {
            clearError(field);
        }

        return isValid;
    }

    /**
     * Validates first/last name.
     */
    public boolean validateName(TextField field, String fieldName) {
        return validateTextField(field, fieldName, value -> {
            if (value.isEmpty()) return fieldName + " is required";
            if (!NAME_PATTERN.matcher(value).matches()) {
                return fieldName + " must be 2-30 letters only";
            }
            return null;
        });
    }

    /**
     * Validates National ID Number.
     */
    public boolean validateNIN(TextField field) {
        return validateTextField(field, "National ID", value -> {
            if (value.isEmpty()) return "National ID is required";
            if (!NIN_PATTERN.matcher(value).matches()) {
                return "NIN must be exactly 14 alphanumeric characters (UPPERCASE)";
            }
            return null;
        });
    }

    /**
     * Validates email format.
     */
    public boolean validateEmail(TextField field) {
        return validateTextField(field, "Email", value -> {
            if (value.isEmpty()) return "Email is required";
            if (!EMAIL_PATTERN.matcher(value).matches()) {
                return "Invalid email format";
            }
            return null;
        });
    }

    /**
     * Validates email confirmation matches.
     */
    public boolean validateEmailMatch(TextField emailField, TextField confirmField) {
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String confirm = confirmField.getText() != null ? confirmField.getText().trim() : "";

        boolean isValid = email.equals(confirm) && !email.isEmpty();
        String error = isValid ? null : "Email addresses do not match";

        results.add(new ValidationResult(isValid, error, confirmField));

        if (!isValid) {
            showError(confirmField, error);
        } else {
            clearError(confirmField);
        }

        return isValid;
    }

    /**
     * Validates Ugandan phone number format.
     */
    public boolean validatePhone(TextField field) {
        return validateTextField(field, "Phone Number", value -> {
            if (value.isEmpty()) return "Phone number is required";
            if (!PHONE_PATTERN.matcher(value).matches()) {
                return "Phone must be +256 followed by 9 digits (e.g., +256772123456)";
            }
            return null;
        });
    }

    /**
     * Validates PIN.
     */
    public boolean validatePIN(TextField field) {
        return validateTextField(field, "PIN", value -> {
            if (value.isEmpty()) return "PIN is required";
            if (!PIN_PATTERN.matcher(value).matches()) {
                return "PIN must be 4-6 digits";
            }
            if (isAllIdentical(value)) {
                return "PIN cannot be all identical digits (e.g., 0000)";
            }
            return null;
        });
    }

    /**
     * Validates PIN confirmation matches.
     */
    public boolean validatePINMatch(TextField pinField, TextField confirmField) {
        String pin = pinField.getText() != null ? pinField.getText().trim() : "";
        String confirm = confirmField.getText() != null ? confirmField.getText().trim() : "";

        boolean isValid = pin.equals(confirm) && !pin.isEmpty();
        String error = isValid ? null : "PINs do not match";

        results.add(new ValidationResult(isValid, error, confirmField));

        if (!isValid) {
            showError(confirmField, error);
        } else {
            clearError(confirmField);
        }

        return isValid;
    }

    /**
     * Validates combo box selection.
     */
    public boolean validateComboBox(ComboBox<?> comboBox, String fieldName) {
        boolean isValid = comboBox.getValue() != null;
        String error = isValid ? null : fieldName + " must be selected";

        results.add(new ValidationResult(isValid, error, comboBox));

        if (!isValid) {
            showError(comboBox, error);
        } else {
            clearError(comboBox);
        }

        return isValid;
    }

    /**
     * Validates opening deposit amount.
     */
    public boolean validateDeposit(TextField field, java.math.BigDecimal minimumDeposit) {
        return validateTextField(field, "Opening Deposit", value -> {
            if (value.isEmpty()) return "Opening deposit is required";
            try {
                java.math.BigDecimal deposit = new java.math.BigDecimal(value.replace(",", ""));
                if (deposit.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    return "Deposit must be greater than zero";
                }
                if (deposit.compareTo(minimumDeposit) < 0) {
                    return String.format("Minimum deposit for this account type is UGX %,d", 
                        minimumDeposit.longValue());
                }
                return null;
            } catch (NumberFormatException e) {
                return "Please enter a valid numeric amount";
            }
        });
    }

    /**
     * Validates age requirements polymorphically.
     */
    public boolean validateAge(int age, Account account, Control control) {
        boolean isValid = account.isAgeValid(age);
        int minAge = 18;
        int maxAge = "Student".equals(account.getAccountType()) ? 25 : 75;
        String error = isValid ? null : 
            String.format("Applicant must be between %d and %d years old for a %s account", 
                minAge, maxAge, account.getAccountType());

        results.add(new ValidationResult(isValid, error, control));

        if (!isValid) {
            showError(control, error);
        } else {
            clearError(control);
        }

        return isValid;
    }

    /**
     * Validates joint account second NIN.
     */
    public boolean validateJointNIN(TextField field) {
        return validateTextField(field, "Joint Account NIN", value -> {
            if (value.isEmpty()) return "Joint account requires a second NIN";
            if (!NIN_PATTERN.matcher(value).matches()) {
                return "Joint NIN must be exactly 14 alphanumeric characters (UPPERCASE)";
            }
            return null;
        });
    }

    private boolean isAllIdentical(String s) {
        if (s.length() < 2) return false;
        char first = s.charAt(0);
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) != first) return false;
        }
        return true;
    }

    private void showError(Control control, String message) {
        Label errorLabel = errorLabels.get(control);
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
        control.setStyle("-fx-border-color: #d32f2f; -fx-border-width: 2px;");
    }

    private void clearError(Control control) {
        Label errorLabel = errorLabels.get(control);
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
        control.setStyle("");
    }

    /**
     * Returns all validation errors.
     */
    public List<String> getAllErrors() {
        List<String> errors = new ArrayList<>();
        for (ValidationResult result : results) {
            if (!result.isValid() && result.getErrorMessage() != null) {
                errors.add(result.getErrorMessage());
            }
        }
        return errors;
    }

    /**
     * Clears all validation state.
     */
    public void clearAll() {
        results.clear();
        for (Map.Entry<Control, Label> entry : errorLabels.entrySet()) {
            clearError(entry.getKey());
        }
    }

    /**
     * Checks if all validations passed.
     */
    public boolean isAllValid() {
        for (ValidationResult result : results) {
            if (!result.isValid()) return false;
        }
        return true;
    }
}
