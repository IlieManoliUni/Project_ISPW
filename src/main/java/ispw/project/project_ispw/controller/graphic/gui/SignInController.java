package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.UserBean; // Use the UserBean from the shared bean package
import ispw.project.project_ispw.exception.ExceptionApplicationController; // Import your custom ApplicationException
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label; // Added for displaying error messages on screen
import javafx.scene.control.PasswordField; // Changed from TextField for password
import javafx.scene.control.TextField;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SignInController implements NavigableController {

    private static final Logger LOGGER = Logger.getLogger(SignInController.class.getName());

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField; // Changed to PasswordField for secure input

    @FXML
    private Button signInButton;

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
        // Clear fields and error messages when the view is initialized/loaded
        clearFields();
    }

    @FXML // Link this method directly in your FXML: onAction="#handleSignInButtonAction"
    private void handleSignInButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Clear any previous error message
        if (errorMessageLabel != null) {
            errorMessageLabel.setText("");
        }

        // Basic input validation at the UI controller level
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Both username and password are required.");
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("Both username and password are required.");
            }
            return;
        }

        try {
            // Create a UserBean from the input fields
            UserBean newUserBean = new UserBean(username, password);

            // Delegate the registration process to the GraphicControllerGui, which in turn
            // delegates to the ApplicationController.
            // The GraphicControllerGui now handles the decision of what screen to show next.
            boolean registrationSuccessful = graphicControllerGui.registerUser(newUserBean);

            if (registrationSuccessful) {
                LOGGER.log(Level.INFO, "User ''{0}'' registered successfully.", username);
                showAlert(Alert.AlertType.INFORMATION, "Registration Success", "User successfully created!");
                // Navigation to 'logIn' screen is now handled by graphicControllerGui.registerUser()
                // No need to call setScreen("logIn") here directly.
            } else {
                // This path should ideally not be reached if ApplicationException is thrown for failures
                // but kept for robustness if graphicControllerGui.registerUser returns false for other reasons.
                LOGGER.log(Level.WARNING, "Registration failed for user {0}: Unknown reason.", username);
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Registration failed. Please try again.");
                if (errorMessageLabel != null) {
                    errorMessageLabel.setText("Registration failed. Please try again.");
                }
            }
        } catch (IllegalArgumentException e) {
            // Catching specific input validation errors that might bubble up from ApplicationController
            LOGGER.log(Level.WARNING, "Registration input validation failed: {0}", e.getMessage());
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
            if (errorMessageLabel != null) {
                errorMessageLabel.setText(e.getMessage());
            }
        } catch (ExceptionApplicationController e) {
            // Catching business logic exceptions from ApplicationController (e.g., username already exists)
            LOGGER.log(Level.WARNING, "Application error during registration for user: " + username, e);
            showAlert(Alert.AlertType.ERROR, "Registration Failed", e.getMessage()); // Show the user-friendly message
            if (errorMessageLabel != null) {
                errorMessageLabel.setText(e.getMessage());
            }
        } catch (Exception e) {
            // Catch any other unexpected errors
            LOGGER.log(Level.SEVERE, "Unexpected error during registration for user: " + username, e);
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred during registration.");
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("An unexpected error occurred.");
            }
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

    // Method to clear fields, useful when navigating to this screen
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        if (errorMessageLabel != null) {
            errorMessageLabel.setText("");
        }
    }
}