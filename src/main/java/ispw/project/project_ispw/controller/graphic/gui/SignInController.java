package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.model.UserModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SignInController implements NavigableController, UserAwareController {

    private static final Logger LOGGER = Logger.getLogger(SignInController.class.getName());
    private static final String SYSTEM_ERROR_TITLE = "System Error";
    private static final String SCREEN_HOME = "home";

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

    @FXML
    private Label errorMessageLabel;

    private GraphicControllerGui graphicControllerGui;
    private UserModel userModel;

    @FXML
    private HBox headerBar;

    @FXML
    private DefaultBackHomeController headerBarController;

    public SignInController() {
        // Empty constructor
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        if (headerBarController != null) {
            headerBarController.setGraphicController(this.graphicControllerGui);
        } else {
            LOGGER.log(Level.WARNING, "Header bar controller is null. The header might not function correctly.");
        }
    }

    @Override
    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;

        if (headerBarController != null) {
            headerBarController.setUserModel(this.userModel);
        }

        userModel.loggedInProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.booleanValue()) {
                graphicControllerGui.setScreen(SCREEN_HOME);
            } else {
                clearFields();
            }
        });

        if (this.userModel.loggedInProperty().get()) {
            graphicControllerGui.setScreen(SCREEN_HOME);
        } else {
            clearFields();
        }
    }

    @FXML
    private void initialize() {
        // Initialization logic
    }

    @FXML
    private void handleSignInButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        clearErrorMessage();

        if (!validateInput(username, password)) {
            return;
        }

        if (checkAlreadyLoggedIn()) {
            return;
        }

        registerUser(username, password);
    }

    private void clearErrorMessage() {
        if (errorMessageLabel != null) {
            errorMessageLabel.setText("");
        }
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("Both username and password are required.");
            }
            showAlert(Alert.AlertType.WARNING, "Input Required", "Both username and password are required.");
            return false;
        }
        return true;
    }

    private boolean checkAlreadyLoggedIn() {
        if (userModel != null && userModel.loggedInProperty().get()) {
            showAlert(Alert.AlertType.INFORMATION, "Already Logged In", "You are already logged in. Redirecting to home.");
            graphicControllerGui.setScreen(SCREEN_HOME);
            return true;
        }
        return false;
    }

    private void registerUser(String username, String password) {
        try {
            UserBean newUserBean = new UserBean(username, password);

            if (userModel == null) {
                LOGGER.log(Level.SEVERE, "UserModel is null. Cannot register user.");
                if (errorMessageLabel != null) {
                    errorMessageLabel.setText("System error: user model not initialized.");
                }
                showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "A system error occurred. Please try again later.");
                return;
            }

            userModel.register(newUserBean);

        } catch (IllegalArgumentException e) {
            if (errorMessageLabel != null) {
                errorMessageLabel.setText(e.getMessage());
            }
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred during registration.", e);
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("An unexpected error occurred.");
            }
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred during registration: " + e.getMessage());
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