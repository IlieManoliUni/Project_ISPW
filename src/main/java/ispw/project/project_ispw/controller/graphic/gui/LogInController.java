package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.controller.graphic.gui.GraphicControllerGui;
import ispw.project.project_ispw.controller.graphic.gui.NavigableController;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label; // Potentially add a Label for error messages on screen

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogInController implements NavigableController {

    private static final Logger LOGGER = Logger.getLogger(LogInController.class.getName());

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button logInButton;

    @FXML
    private Button signUpButton;

    @FXML
    private Label errorMessageLabel; // Added for displaying error messages directly on the UI

    // Reference to the GraphicControllerGui (Screen Manager)
    private GraphicControllerGui graphicControllerGui;

    // This method is called by the GraphicControllerGui when this controller's FXML is loaded
    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;
    }

    @FXML
    private void initialize() {
        // You can keep `initialize` if you have specific setup for FXML elements.
        // For button actions, often direct FXML methods are cleaner than setOnAction in initialize
        // if the actions are simple and don't require complex setup logic.
        // However, your current setOnAction is perfectly fine.

        // Example: If you want to clear fields when the view is loaded (e.g., when returning to login)
        // This is a good place to do it.
        clearFields();
    }


    @FXML // Link this method directly in your FXML: onAction="#handleLoginButtonAction"
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Clear any previous error message
        if (errorMessageLabel != null) {
            errorMessageLabel.setText("");
        }

        // Basic input validation at the UI controller level
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter both username and password.");
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("Please enter both username and password.");
            }
            return;
        }

        try {
            // Delegate the login process to the GraphicControllerGui, which in turn
            // delegates to the ApplicationController.
            // The GraphicControllerGui now handles the decision of what screen to show next.
            boolean loginSuccessful = graphicControllerGui.processLogin(username, password);

            if (loginSuccessful) {
                LOGGER.log(Level.INFO, "Login successful for user: {0}", username);
                // Navigation to 'home' screen is now handled by graphicControllerGui.processLogin()
                // No need to call setScreen("home") here directly.
            } else {
                // Login failed due to invalid credentials (ApplicationController returned false)
                LOGGER.log(Level.INFO, "Login failed for user {0}: Invalid credentials.", username);
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password!");
                if (errorMessageLabel != null) {
                    errorMessageLabel.setText("Invalid username or password!");
                }
            }
        } catch (IllegalArgumentException e) {
            // Catching specific input validation errors that might bubble up from ApplicationController
            LOGGER.log(Level.WARNING, "Login input validation failed: {0}", e.getMessage());
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
            if (errorMessageLabel != null) {
                errorMessageLabel.setText(e.getMessage());
            }
        } catch (Exception e) {
            // Catch any other unexpected errors
            LOGGER.log(Level.SEVERE, "Unexpected error during login for user: " + username, e);
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred during login.");
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("An unexpected error occurred.");
            }
        }
    }

    @FXML // Link this method directly in your FXML: onAction="#handleSignUpButtonAction"
    private void handleSignUpButtonAction() {
        try {
            // Request navigation to the sign-in/sign-up page from the Screen Manager
            graphicControllerGui.setScreen("signIn");
        } catch (Exception e) { // Catch generic Exception if setScreen might throw
            LOGGER.log(Level.SEVERE, "Failed to navigate to sign-up page.", e);
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to navigate to sign-up page.");
        }
    }

    // Helper method to show alert messages
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // You can add a method to clear fields, useful when navigating back to this screen
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        if (errorMessageLabel != null) {
            errorMessageLabel.setText("");
        }
    }
}