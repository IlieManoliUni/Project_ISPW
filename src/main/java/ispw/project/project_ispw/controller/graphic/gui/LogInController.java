package ispw.project.project_ispw.controller.graphic.gui;

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

public class LogInController implements NavigableController, UserAwareController {

    private static final Logger LOGGER = Logger.getLogger(LogInController.class.getName());
    private static final String SYSTEM_ERROR_TITLE = "System Error";
    private static final String SCREEN_HOME = "home";

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signInButton;

    @FXML
    private Label errorMessageLabel; // Keep this for binding

    private GraphicControllerGui graphicControllerGui;
    private UserModel userModel;

    @FXML
    private HBox headerBar; // Assuming you include default.fxml

    @FXML
    private DefaultBackHomeController headerBarController; // <-- Corrected type to DefaultController


    public LogInController() {
        // Empty constructor
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        if (headerBarController != null) {
            headerBarController.setGraphicController(this.graphicControllerGui);
        } else {
            LOGGER.log(Level.WARNING, "Header bar controller is null in LogInController. The header might not function correctly.");
        }
    }

    @Override
    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;

        if (headerBarController != null) {
            headerBarController.setUserModel(this.userModel);
        }

        // --- NEW: Bind errorMessageLabel to userModel's authStatusMessageProperty ---
        if (errorMessageLabel != null) {
            errorMessageLabel.textProperty().bind(userModel.authStatusMessageProperty());
        }

        // --- Handle navigation based on login status ---
        // This listener will navigate to HOME immediately if login succeeds
        userModel.loggedInProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.booleanValue()) {
                graphicControllerGui.setScreen(SCREEN_HOME);
            } else {
                clearFields(); // Clear fields if logout occurs or login fails
            }
        });

        // --- Initial check on load ---
        if (this.userModel.loggedInProperty().get()) {
            graphicControllerGui.setScreen(SCREEN_HOME); // If already logged in, go home immediately
        } else {
            clearFields(); // Clear fields if not logged in initially
        }
    }

    @FXML
    private void initialize() {
        // Initialization logic
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Clear previous error message if not bound (or if binding is cleared on successful input)
        if (errorMessageLabel != null && errorMessageLabel.textProperty().isBound()) {
            errorMessageLabel.textProperty().unbind(); // Unbind temporarily to clear
            errorMessageLabel.setText("");
            errorMessageLabel.textProperty().bind(userModel.authStatusMessageProperty()); // Rebind
        } else if (errorMessageLabel != null) {
            errorMessageLabel.setText(""); // If not bound, just clear it
        }


        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter both username and password.");
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("Please enter both username and password.");
            }
            return;
        }

        if (userModel == null) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "User model not initialized.");
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("System error: User model not initialized.");
            }
            return;
        }

        // --- CRITICAL CHANGE HERE: Call userModel.login() directly ---
        userModel.login(username, password);
        // The outcome (success/failure) and corresponding navigation/error messages
        // are now handled reactively by the UserModel's properties and their listeners
        // in setUserModel().
    }

    @FXML
    private void handleSignInButtonAction() {
        graphicControllerGui.setScreen("signIn");
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
        // Error message label is now bound, so it will update automatically.
        // We might want to explicitly clear the auth status message in UserModel on certain UI actions.
        if (errorMessageLabel != null && errorMessageLabel.textProperty().isBound()) {
            userModel.authStatusMessageProperty().set(""); // Clear the message via the model
        } else if (errorMessageLabel != null) {
            errorMessageLabel.setText("");
        }
    }
}