package ispw.project.project_ispw.model;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.controller.application.util.AuthService;
import ispw.project.project_ispw.exception.ExceptionApplicationController;

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

public class UserModel {

    private static final Logger logger = Logger.getLogger(UserModel.class.getName());

    private final AuthService authService;
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

    // In UserModel.java

    public void register(UserBean newUserBean) {
        authStatusMessage.set("Attempting registration...");
        authErrorMessage.set(null);

        executorService.submit(() -> {
            try {
                boolean registrationSuccess = authService.registerUser(newUserBean); // Calls AuthService to register

                Platform.runLater(() -> {
                    if (registrationSuccess) {
                        // --- THE CORRECTED AUTO-LOGIN LOGIC WITHIN USERMODEL ---
                        // After successful registration, use the UserModel's own login method.
                        // This will correctly update currentUserProperty and loggedInProperty.
                        login(newUserBean.getUsername(), newUserBean.getPassword()); // Calls UserModel's login method
                        authStatusMessage.set("Registration successful. Logging in...");
                        // --- END CORRECTED AUTO-LOGIN LOGIC ---
                    } else {
                        authStatusMessage.set("Registration failed.");
                        // Specific messages might be handled by AuthService and propagated
                    }
                });
            } catch (ExceptionApplicationController e) {
                Platform.runLater(() -> {
                    authStatusMessage.set("Registration failed.");
                    authErrorMessage.set(e.getMessage());
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