package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignInController implements NavigableController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

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
        } else {
            // Error handling for when the included controller is null
        }
    }

    @FXML
    private void initialize() {
        clearFields();
    }

    @FXML
    private void handleSignInButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (errorMessageLabel != null) {
            errorMessageLabel.setText("");
        }

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Both username and password are required.");
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("Both username and password are required.");
            }
            return;
        }

        try {
            UserBean newUserBean = new UserBean(username, password);

            // Call registerUser, which now handles the automatic login and navigation
            boolean registrationAndLoginSuccessful = graphicControllerGui.registerUser(newUserBean);

            if (!registrationAndLoginSuccessful) {
                showAlert(Alert.AlertType.ERROR, "Operation Failed", "Registration or automatic login failed. Please try again or log in manually.");
                if (errorMessageLabel != null) {
                    errorMessageLabel.setText("Registration or automatic login failed. Please try again.");
                }
            }
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
            if (errorMessageLabel != null) {
                errorMessageLabel.setText(e.getMessage());
            }
        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "Operation Failed", e.getMessage());
            if (errorMessageLabel != null) {
                errorMessageLabel.setText(e.getMessage());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred during registration or login.");
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("An unexpected error occurred.");
            }
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