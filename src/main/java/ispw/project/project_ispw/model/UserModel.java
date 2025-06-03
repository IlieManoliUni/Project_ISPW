package ispw.project.project_ispw.model;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.controller.application.util.AuthService;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * UserModel class acts as a data model responsible for managing the application's user session and authentication state.
 * It provides observable JavaFX properties for UI binding, allowing views to react to changes in login status,
 * current user information, and authentication-related messages.
 */
public class UserModel {

    private static final Logger logger = Logger.getLogger(UserModel.class.getName());

    private final AuthService authService;
    //Perform operations on separated threads
    private final ExecutorService executorService;

    private final ObjectProperty<UserBean> currentUserProperty = new SimpleObjectProperty<>();
    private final BooleanProperty loggedInProperty = new SimpleBooleanProperty(false);
    private final StringProperty usernameDisplayProperty = new SimpleStringProperty("");
    private final StringProperty authStatusMessage = new SimpleStringProperty("");
    private final StringProperty authErrorMessage = new SimpleStringProperty("");

    public UserModel(AuthService authService) {
        this.authService = authService;
        this.executorService = Executors.newSingleThreadExecutor();

        currentUserProperty.addListener((obs, oldUser, newUser) -> {
            boolean isLoggedIn = newUser != null;
            loggedInProperty.set(isLoggedIn);
            usernameDisplayProperty.set(isLoggedIn ? newUser.getUsername() : "");
            authStatusMessage.set(isLoggedIn ? "Logged in as " + newUser.getUsername() : "");
            authErrorMessage.set(null);
        });
    }

    public void login(String username, String password) {
        authStatusMessage.set("Attempting login...");
        authErrorMessage.set(null);

        executorService.submit(() -> {
            try {
                boolean success = authService.login(username, password);
                Platform.runLater(() -> {
                    if (success) {
                        currentUserProperty.set(authService.getCurrentUser());
                        authStatusMessage.set("Login successful!");
                    } else {
                        currentUserProperty.set(null);
                        authStatusMessage.set("Login failed. Invalid username or password.");
                        authErrorMessage.set("Invalid credentials.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    currentUserProperty.set(null);
                    authStatusMessage.set("An unexpected error occurred during login.");
                    authErrorMessage.set("Error: " + e.getMessage());
                    logger.log(Level.SEVERE, e, () -> "Login error: " + e.getMessage());
                });
            }
        });
    }

    public void register(UserBean newUserBean) {
        authStatusMessage.set("Attempting registration...");
        authErrorMessage.set(null);

        executorService.submit(() -> {
            try {
                boolean registrationSuccess = authService.registerUser(newUserBean);
                Platform.runLater(() -> {
                    if (registrationSuccess) {

                        login(newUserBean.getUsername(), newUserBean.getPassword());
                        authStatusMessage.set("Registration successful. Logging in...");
                    } else {
                        authStatusMessage.set("Registration failed.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    authStatusMessage.set("An unexpected error occurred during registration.");
                    authErrorMessage.set("Error: " + e.getMessage());
                    logger.log(Level.SEVERE, e, () -> "Registration error: " + e.getMessage());
                });
            }
        });
    }

    public void logout() {
        executorService.submit(() -> {
            try {
                authService.logout();
                Platform.runLater(() -> {
                    currentUserProperty.set(null);
                    authStatusMessage.set("Logged out.");
                    authErrorMessage.set(null);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    authStatusMessage.set("An error occurred during logout.");
                    authErrorMessage.set("Logout error: " + e.getMessage());
                });
            }
        });
    }

    public ObjectProperty<UserBean> currentUserProperty() {
        return currentUserProperty;
    }

    public BooleanProperty loggedInProperty() {
        return loggedInProperty;
    }

    public StringProperty usernameDisplayProperty() {
        return usernameDisplayProperty;
    }

    public StringProperty authStatusMessageProperty() {
        return authStatusMessage;
    }

    public StringProperty authErrorMessageProperty() {
        return authErrorMessage;
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}