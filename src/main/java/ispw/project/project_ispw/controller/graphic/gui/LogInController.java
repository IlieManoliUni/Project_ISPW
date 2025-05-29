package ispw.project.project_ispw.controller.graphic.gui;

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

    private GraphicControllerGui graphicControllerGui;

    @FXML
    private DefaultBackHomeController headerBarController;

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        if (headerBarController != null) {
            headerBarController.setGraphicController(this.graphicControllerGui);
        }
    }

    @FXML
    private void initialize() {
        clearFields();
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (errorMessageLabel != null) {
            errorMessageLabel.setText("");
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
        } catch (Exception _) {
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
        } catch (Exception _) {
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