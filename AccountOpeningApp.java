package com.firstbank.uganda;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.firstbank.uganda.model.*;
import com.firstbank.uganda.validation.Validator;
import com.firstbank.uganda.dao.DatabaseManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

/**
 * First Bank Uganda - New Account Opening Application
 * JavaFX desktop application with comprehensive validation and data persistence.
 * 
 * Built with Java 17 and JavaFX 19. Uses H2 embedded database for portability.
 * 
 * @author Development Team
 * @version 1.0
 */
public class AccountOpeningApp extends Application {

    // Form fields
    private TextField txtFirstName, txtLastName, txtNIN, txtEmail, txtConfirmEmail;
    private TextField txtPhone, txtPIN, txtConfirmPIN, txtOpeningDeposit, txtJointNIN;
    private ComboBox<Integer> cmbYear, cmbDay;
    private ComboBox<String> cmbMonth;
    private ComboBox<String> cmbAccountType, cmbBranch;
    private TextArea txtAccountSummary;
    private Label lblAgeDisplay;

    // Error labels
    private Label errFirstName, errLastName, errNIN, errEmail, errConfirmEmail;
    private Label errPhone, errPIN, errConfirmPIN, errDOB, errAccountType, errBranch;
    private Label errDeposit, errAge, errJointNIN;

    // Layout containers
    private TitledPane jointPane;

    private Validator validator;
    private DatabaseManager dbManager;

    @Override
    public void start(Stage primaryStage) {
        validator = new Validator();
        dbManager = DatabaseManager.getInstance();

        primaryStage.setTitle("First Bank Uganda - New Account Opening");
        primaryStage.setMinWidth(950);
        primaryStage.setMinHeight(850);

        // Main container with bank branding colors
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Header with bank branding
        root.getChildren().add(createHeader());

        // Main form
        ScrollPane scrollPane = new ScrollPane(createForm());
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f5f5;");
        root.getChildren().add(scrollPane);

        Scene scene = new Scene(root);

        // Try to load stylesheet, fallback to inline if not found
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Stylesheet not found, using default styling");
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #1a5276; -fx-background-radius: 5;");

        Label lblTitle = new Label("FIRST BANK UGANDA");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 26));
        lblTitle.setTextFill(Color.WHITE);

        Label lblSubtitle = new Label("New Account Opening Application");
        lblSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        lblSubtitle.setTextFill(Color.LIGHTGRAY);

        header.getChildren().addAll(lblTitle, lblSubtitle);
        return header;
    }

    private VBox createForm() {
        VBox form = new VBox(12);
        form.setPadding(new Insets(10));

        // Personal Information Section
        TitledPane personalPane = new TitledPane("Personal Information", createPersonalInfoSection());
        personalPane.setExpanded(true);
        personalPane.setCollapsible(false);

        // Contact Information Section
        TitledPane contactPane = new TitledPane("Contact Information", createContactSection());
        contactPane.setExpanded(true);
        contactPane.setCollapsible(false);

        // Security Section
        TitledPane securityPane = new TitledPane("Security (PIN)", createSecuritySection());
        securityPane.setExpanded(true);
        securityPane.setCollapsible(false);

        // Date of Birth Section
        TitledPane dobPane = new TitledPane("Date of Birth", createDOBSection());
        dobPane.setExpanded(true);
        dobPane.setCollapsible(false);

        // Account Details Section
        TitledPane accountPane = new TitledPane("Account Details", createAccountSection());
        accountPane.setExpanded(true);
        accountPane.setCollapsible(false);

        // Joint Account Section (conditionally shown)
        jointPane = new TitledPane("Joint Account Details", createJointSection());
        jointPane.setExpanded(true);
        jointPane.setCollapsible(false);
        jointPane.setVisible(false);
        jointPane.setManaged(false);

        // Buttons
        HBox buttonBox = createButtonBox();

        // Account Summary
        VBox summaryBox = createSummarySection();

        form.getChildren().addAll(
            personalPane, contactPane, securityPane, 
            dobPane, accountPane, jointPane, 
            buttonBox, summaryBox
        );

        return form;
    }

    private GridPane createPersonalInfoSection() {
        GridPane grid = createStandardGrid();

        txtFirstName = new TextField();
        txtFirstName.setPromptText("e.g., Allan");
        errFirstName = createErrorLabel();

        txtLastName = new TextField();
        txtLastName.setPromptText("e.g., Okello");
        errLastName = createErrorLabel();

        txtNIN = new TextField();
        txtNIN.setPromptText("14 characters UPPERCASE");
        errNIN = createErrorLabel();

        grid.addRow(0, new Label("First Name *:"), txtFirstName, errFirstName);
        grid.addRow(1, new Label("Last Name *:"), txtLastName, errLastName);
        grid.addRow(2, new Label("National ID (NIN) *:"), txtNIN, errNIN);

        validator.registerErrorLabel(txtFirstName, errFirstName);
        validator.registerErrorLabel(txtLastName, errLastName);
        validator.registerErrorLabel(txtNIN, errNIN);

        return grid;
    }

    private GridPane createContactSection() {
        GridPane grid = createStandardGrid();

        txtEmail = new TextField();
        txtEmail.setPromptText("e.g., name@email.com");
        errEmail = createErrorLabel();

        txtConfirmEmail = new TextField();
        txtConfirmEmail.setPromptText("Re-enter email");
        errConfirmEmail = createErrorLabel();

        txtPhone = new TextField();
        txtPhone.setPromptText("+256XXXXXXXXX");
        errPhone = createErrorLabel();

        grid.addRow(0, new Label("Email *:"), txtEmail, errEmail);
        grid.addRow(1, new Label("Confirm Email *:"), txtConfirmEmail, errConfirmEmail);
        grid.addRow(2, new Label("Phone Number *:"), txtPhone, errPhone);

        validator.registerErrorLabel(txtEmail, errEmail);
        validator.registerErrorLabel(txtConfirmEmail, errConfirmEmail);
        validator.registerErrorLabel(txtPhone, errPhone);

        return grid;
    }

    private GridPane createSecuritySection() {
        GridPane grid = createStandardGrid();

        txtPIN = new PasswordField();
        txtPIN.setPromptText("4-6 digits");
        errPIN = createErrorLabel();

        txtConfirmPIN = new PasswordField();
        txtConfirmPIN.setPromptText("Re-enter PIN");
        errConfirmPIN = createErrorLabel();

        Label lblHint = new Label("PIN cannot be all identical digits (e.g., 0000)");
        lblHint.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        lblHint.setWrapText(true);

        grid.addRow(0, new Label("PIN *:"), txtPIN, errPIN);
        grid.addRow(1, new Label("Confirm PIN *:"), txtConfirmPIN, errConfirmPIN);
        grid.add(lblHint, 1, 2, 2, 1);

        validator.registerErrorLabel(txtPIN, errPIN);
        validator.registerErrorLabel(txtConfirmPIN, errConfirmPIN);

        return grid;
    }

    private GridPane createDOBSection() {
        GridPane grid = createStandardGrid();

        // Year combo (1900 to current year - 18)
        cmbYear = new ComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 75; y <= currentYear - 18; y++) {
            cmbYear.getItems().add(y);
        }
        cmbYear.setPromptText("Year");

        // Month combo
        cmbMonth = new ComboBox<>();
        List<String> months = Arrays.asList(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        );
        cmbMonth.getItems().addAll(months);
        cmbMonth.setPromptText("Month");

        // Day combo
        cmbDay = new ComboBox<>();
        cmbDay.setPromptText("Day");

        // Auto-update days when month/year changes
        cmbYear.valueProperty().addListener((obs, old, val) -> updateDays());
        cmbMonth.valueProperty().addListener((obs, old, val) -> updateDays());

        lblAgeDisplay = new Label();
        lblAgeDisplay.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a5276;");

        errDOB = createErrorLabel();

        HBox dobBox = new HBox(10, cmbYear, cmbMonth, cmbDay);
        dobBox.setAlignment(Pos.CENTER_LEFT);

        grid.addRow(0, new Label("Date of Birth *:"), dobBox, errDOB);
        grid.addRow(1, new Label("Age:"), lblAgeDisplay);

        // Age calculation listener
        cmbYear.valueProperty().addListener((obs, old, val) -> calculateAge());
        cmbMonth.valueProperty().addListener((obs, old, val) -> calculateAge());
        cmbDay.valueProperty().addListener((obs, old, val) -> calculateAge());

        validator.registerErrorLabel(cmbYear, errDOB);

        return grid;
    }

    private void updateDays() {
        Integer year = cmbYear.getValue();
        String month = cmbMonth.getValue();

        if (year == null || month == null) return;

        int monthNum = cmbMonth.getItems().indexOf(month) + 1;
        YearMonth yearMonth = YearMonth.of(year, monthNum);
        int daysInMonth = yearMonth.lengthOfMonth();

        Integer selectedDay = cmbDay.getValue();
        cmbDay.getItems().clear();
        for (int d = 1; d <= daysInMonth; d++) {
            cmbDay.getItems().add(d);
        }

        // Restore selection if valid
        if (selectedDay != null && selectedDay <= daysInMonth) {
            cmbDay.setValue(selectedDay);
        }
    }

    private void calculateAge() {
        Integer year = cmbYear.getValue();
        String month = cmbMonth.getValue();
        Integer day = cmbDay.getValue();

        if (year == null || month == null || day == null) {
            lblAgeDisplay.setText("");
            return;
        }

        int monthNum = cmbMonth.getItems().indexOf(month) + 1;
        LocalDate dob = LocalDate.of(year, monthNum, day);
        int age = LocalDate.now().getYear() - dob.getYear();
        if (LocalDate.now().getDayOfYear() < dob.getDayOfYear()) {
            age--;
        }

        lblAgeDisplay.setText(age + " years old");

        // Update age validation display
        String accountType = cmbAccountType.getValue();
        if ("Student".equals(accountType)) {
            boolean valid = age >= 18 && age <= 25;
            lblAgeDisplay.setStyle(valid ? 
                "-fx-font-weight: bold; -fx-text-fill: #27ae60;" : 
                "-fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        } else {
            boolean valid = age >= 18 && age <= 75;
            lblAgeDisplay.setStyle(valid ? 
                "-fx-font-weight: bold; -fx-text-fill: #27ae60;" : 
                "-fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        }
    }

    private GridPane createAccountSection() {
        GridPane grid = createStandardGrid();

        cmbAccountType = new ComboBox<>();
        cmbAccountType.getItems().addAll(
            "Savings", "Current", "Fixed Deposit", "Student", "Joint"
        );
        cmbAccountType.setPromptText("Select Account Type");
        errAccountType = createErrorLabel();

        cmbBranch = new ComboBox<>();
        for (Branch b : Branch.values()) {
            cmbBranch.getItems().add(b.getDisplayName());
        }
        cmbBranch.setPromptText("Select Branch");
        errBranch = createErrorLabel();

        txtOpeningDeposit = new TextField();
        txtOpeningDeposit.setPromptText("UGX amount");
        errDeposit = createErrorLabel();

        // Show/hide joint section based on account type
        cmbAccountType.valueProperty().addListener((obs, old, val) -> {
            boolean isJoint = "Joint".equals(val);
            jointPane.setVisible(isJoint);
            jointPane.setManaged(isJoint);
            updateDepositPrompt(val);
            calculateAge(); // Re-validate age display
        });

        grid.addRow(0, new Label("Account Type *:"), cmbAccountType, errAccountType);
        grid.addRow(1, new Label("Branch *:"), cmbBranch, errBranch);
        grid.addRow(2, new Label("Opening Deposit (UGX) *:"), txtOpeningDeposit, errDeposit);

        validator.registerErrorLabel(cmbAccountType, errAccountType);
        validator.registerErrorLabel(cmbBranch, errBranch);
        validator.registerErrorLabel(txtOpeningDeposit, errDeposit);

        return grid;
    }

    private void updateDepositPrompt(String accountType) {
        if (accountType == null) return;
        switch (accountType) {
            case "Savings": txtOpeningDeposit.setPromptText("Min: UGX 50,000"); break;
            case "Current": txtOpeningDeposit.setPromptText("Min: UGX 200,000"); break;
            case "Fixed Deposit": txtOpeningDeposit.setPromptText("Min: UGX 1,000,000"); break;
            case "Student": txtOpeningDeposit.setPromptText("Min: UGX 10,000"); break;
            case "Joint": txtOpeningDeposit.setPromptText("Min: UGX 100,000"); break;
        }
    }

    private GridPane createJointSection() {
        GridPane grid = createStandardGrid();

        txtJointNIN = new TextField();
        txtJointNIN.setPromptText("Second applicant's NIN (14 chars UPPERCASE)");
        errJointNIN = createErrorLabel();

        Label lblInfo = new Label("Joint accounts require a second National ID Number");
        lblInfo.setStyle("-fx-font-size: 11; -fx-text-fill: #666; -fx-font-style: italic;");
        lblInfo.setWrapText(true);

        grid.addRow(0, new Label("Second Applicant NIN *:"), txtJointNIN, errJointNIN);
        grid.add(lblInfo, 1, 1, 2, 1);

        validator.registerErrorLabel(txtJointNIN, errJointNIN);

        return grid;
    }

    private HBox createButtonBox() {
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));

        Button btnSubmit = new Button("Submit Application");
        btnSubmit.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                          "-fx-font-weight: bold; -fx-padding: 12 30; -fx-font-size: 14;");
        btnSubmit.setOnAction(e -> handleSubmit());

        Button btnReset = new Button("Reset Form");
        btnReset.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                         "-fx-font-weight: bold; -fx-padding: 12 30; -fx-font-size: 14;");
        btnReset.setOnAction(e -> handleReset());

        box.getChildren().addAll(btnSubmit, btnReset);
        return box;
    }

    private VBox createSummarySection() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");

        Label lblTitle = new Label("Account Summary is Below:");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblTitle.setStyle("-fx-text-fill: #1a5276;");

        txtAccountSummary = new TextArea();
        txtAccountSummary.setEditable(false);
        txtAccountSummary.setPrefRowCount(6);
        txtAccountSummary.setWrapText(true);
        txtAccountSummary.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12;");

        box.getChildren().addAll(lblTitle, txtAccountSummary);
        return box;
    }

    private GridPane createStandardGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.setAlignment(Pos.CENTER_LEFT);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        col1.setPrefWidth(180);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(250);
        col2.setPrefWidth(300);
        col2.setHgrow(Priority.ALWAYS);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setMinWidth(200);
        col3.setPrefWidth(250);

        grid.getColumnConstraints().addAll(col1, col2, col3);
        return grid;
    }

    private Label createErrorLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 11;");
        label.setWrapText(true);
        label.setVisible(false);
        label.setManaged(false);
        return label;
    }

    private void handleSubmit() {
        validator.clearAll();

        // Automatically uppercase and trim NIN fields before validation
        if (txtNIN.getText() != null) {
            txtNIN.setText(txtNIN.getText().trim().toUpperCase());
        }
        if (txtJointNIN.getText() != null) {
            txtJointNIN.setText(txtJointNIN.getText().trim().toUpperCase());
        }

        // Validate all fields
        boolean valid = true;

        valid &= validator.validateName(txtFirstName, "First Name");
        valid &= validator.validateName(txtLastName, "Last Name");
        valid &= validator.validateNIN(txtNIN);
        valid &= validator.validateEmail(txtEmail);
        valid &= validator.validateEmailMatch(txtEmail, txtConfirmEmail);
        valid &= validator.validatePhone(txtPhone);
        valid &= validator.validatePIN(txtPIN);
        valid &= validator.validatePINMatch(txtPIN, txtConfirmPIN);

        // DOB validation
        valid &= validator.validateComboBox(cmbYear, "Year of birth");
        valid &= validator.validateComboBox(cmbMonth, "Month of birth");
        valid &= validator.validateComboBox(cmbDay, "Day of birth");

        // Account type and branch
        valid &= validator.validateComboBox(cmbAccountType, "Account Type");
        valid &= validator.validateComboBox(cmbBranch, "Branch");

        // Get account type for polymorphic validation
        String accountTypeStr = cmbAccountType.getValue();
        Account tempAccount = AccountFactory.createAccount(accountTypeStr != null ? accountTypeStr : "Savings");

        // Deposit validation
        valid &= validator.validateDeposit(txtOpeningDeposit, tempAccount.getMinimumDeposit());

        // Age validation using polymorphism
        int age = 0;
        if (cmbYear.getValue() != null && cmbMonth.getValue() != null && cmbDay.getValue() != null) {
            int monthNum = cmbMonth.getItems().indexOf(cmbMonth.getValue()) + 1;
            LocalDate dob = LocalDate.of(cmbYear.getValue(), monthNum, cmbDay.getValue());
            age = LocalDate.now().getYear() - dob.getYear();
            if (LocalDate.now().getDayOfYear() < dob.getDayOfYear()) age--;
        }

        valid &= validator.validateAge(age, tempAccount, cmbYear);

        // Joint NIN validation
        if ("Joint".equals(accountTypeStr)) {
            valid &= validator.validateJointNIN(txtJointNIN);
        }

        if (!valid) {
            showValidationErrors();
            return;
        }

        // All valid - create and save account
        try {
            Account account = createAccountFromForm();
            String accountNumber = dbManager.generateAccountNumber(account.getBranchCode());
            account.setAccountNumber(accountNumber);

            dbManager.saveAccount(account);

            // Display success
            txtAccountSummary.setText(account.getFormattedRecord());
            txtAccountSummary.setStyle("-fx-font-family: 'Consolas', monospace; " +
                                      "-fx-font-size: 12; -fx-text-fill: #27ae60; " +
                                      "-fx-control-inner-background: #e8f8f5;");

            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "Account created successfully!\n\nAccount Number: " + accountNumber);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                "Failed to save account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Account createAccountFromForm() {
        String accountTypeStr = cmbAccountType.getValue();
        Account account = AccountFactory.createAccount(accountTypeStr);

        account.setFirstName(txtFirstName.getText().trim());
        account.setLastName(txtLastName.getText().trim());
        account.setNin(txtNIN.getText().trim().toUpperCase());
        account.setEmail(txtEmail.getText().trim());
        account.setPhoneNumber(txtPhone.getText().trim());
        account.setPin(txtPIN.getText().trim());

        int monthNum = cmbMonth.getItems().indexOf(cmbMonth.getValue()) + 1;
        account.setDateOfBirth(LocalDate.of(cmbYear.getValue(), monthNum, cmbDay.getValue()));

        String branchName = cmbBranch.getValue();
        for (Branch b : Branch.values()) {
            if (b.getDisplayName().equals(branchName)) {
                account.setBranchCode(b.getCode());
                account.setBranchName(b.getDisplayName());
                break;
            }
        }

        account.setOpeningDeposit(new BigDecimal(txtOpeningDeposit.getText().trim().replace(",", "")));

        if ("Joint".equals(accountTypeStr)) {
            account.setJointNin(txtJointNIN.getText().trim().toUpperCase());
        }

        return account;
    }

    private void showValidationErrors() {
        List<String> errors = validator.getAllErrors();
        StringBuilder sb = new StringBuilder("Please correct the following errors:\n\n");
        for (int i = 0; i < errors.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, errors.get(i)));
        }

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Errors");
        alert.setHeaderText("The form contains errors");
        alert.setContentText(sb.toString());
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    private void handleReset() {
        // Clear all fields
        txtFirstName.clear();
        txtLastName.clear();
        txtNIN.clear();
        txtEmail.clear();
        txtConfirmEmail.clear();
        txtPhone.clear();
        txtPIN.clear();
        txtConfirmPIN.clear();
        txtOpeningDeposit.clear();
        txtJointNIN.clear();

        cmbYear.setValue(null);
        cmbMonth.setValue(null);
        cmbDay.setValue(null);
        cmbAccountType.setValue(null);
        cmbBranch.setValue(null);

        txtAccountSummary.clear();
        txtAccountSummary.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12;");

        lblAgeDisplay.setText("");

        validator.clearAll();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        if (dbManager != null) {
            dbManager.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
