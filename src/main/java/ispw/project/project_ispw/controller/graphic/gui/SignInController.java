package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SignInController implements NavigableController {

    private static final Logger LOGGER = Logger.getLogger(SignInController.class.getName());

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
            LOGGER.log(Level.WARNING, "Header bar controller is null. The header might not function correctly.");
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

        errorMessageLabel.setText("");

        if (username.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("Both username and password are required.");
            showAlert(Alert.AlertType.WARNING, "Input Required", "Both username and password are required.");
            return;
        }

        try {
            UserBean newUserBean = new UserBean(username, password);

            boolean registrationAndLoginSuccessful = graphicControllerGui.registerUser(newUserBean);

            if (!registrationAndLoginSuccessful) {
                String message = "Registration or automatic login failed. Please try again or log in manually.";
                errorMessageLabel.setText("Registration or automatic login failed. Please try again.");
                showAlert(Alert.AlertType.ERROR, "Operation Failed", message);
            }
        } catch (IllegalArgumentException e) {
            errorMessageLabel.setText(e.getMessage());
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        } catch (ExceptionApplicationController e) {
            errorMessageLabel.setText(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Operation Failed", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred during registration or login.", e);
            errorMessageLabel.setText("An unexpected error occurred.");
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred during registration or login.");
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