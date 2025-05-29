package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.exception.ExceptionApplicationController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class LogInController implements NavigableController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button logInButton;

    @FXML
    private Button signUpButton;

    @FXML
    private Label errorMessageLabel;

    // Reference to the GraphicControllerGui (Screen Manager)
    private GraphicControllerGui graphicControllerGui;

    // --- NEW: FXML injection for the included DefaultBackHomeController ---
    // This field will be automatically populated by FXMLLoader
    // if 'logIn.fxml' has <fx:include fx:id="headerBar" .../>
    @FXML
    private DefaultBackHomeController headerBarController; // Assuming fx:id="headerBar" in logIn.fxml
    // --- END NEW ---

    // This method is called by the GraphicControllerGui when this controller's FXML is loaded
    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        // --- NEW: Manually inject GraphicControllerGui into the DefaultBackHomeController ---
        // This is crucial because DefaultBackHomeController is embedded via fx:include
        if (headerBarController != null) {
            headerBarController.setGraphicController(this.graphicControllerGui);
        }
        // --- END NEW ---
    }

    @FXML
    private void initialize() {
        // This method is called by FXMLLoader after all @FXML annotated fields are populated.
        // It runs BEFORE setGraphicController().
        clearFields(); // Clear fields when the view is loaded
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (errorMessageLabel != null) {
            errorMessageLabel.setText(""); // Clear any previous error message
        }

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter both username and password.");
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("Please enter both username and password.");
            }
            return;
        }

        try {
            boolean loginSuccessful = graphicControllerGui.processLogin(username, password);

            if (!loginSuccessful) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password!");
                if (errorMessageLabel != null) {
                    errorMessageLabel.setText("Invalid username or password!");
                }
            }
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
            if (errorMessageLabel != null) {
                errorMessageLabel.setText(e.getMessage());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred during login.");
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("An unexpected error occurred.");
            }
        }
    }

    @FXML
    private void handleSignUpButtonAction() {
        try {
            graphicControllerGui.setScreen("signIn");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to navigate to sign-up page.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clearFields() {
        if (usernameField != null) usernameField.setText("");
        if (passwordField != null) passwordField.setText("");
        if (errorMessageLabel != null) errorMessageLabel.setText("");
    }
}