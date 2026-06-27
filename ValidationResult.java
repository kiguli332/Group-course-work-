package com.firstbank.uganda.validation;

import javafx.scene.control.Control;

/**
 * Holds the result of a single field validation.
 */
public class ValidationResult {
    private final boolean valid;
    private final String errorMessage;
    private final Control control;

    public ValidationResult(boolean valid, String errorMessage, Control control) {
        this.valid = valid;
        this.errorMessage = errorMessage;
        this.control = control;
    }

    public boolean isValid() { return valid; }
    public String getErrorMessage() { return errorMessage; }
    public Control getControl() { return control; }
}
