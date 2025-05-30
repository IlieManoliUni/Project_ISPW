package ispw.project.project_ispw.model; // Assuming this package for your application's models

import ispw.project.project_ispw.bean.UserBean; // Your existing UserBean
import ispw.project.project_ispw.controller.application.util.AuthService; // Your existing AuthService
import ispw.project.project_ispw.exception.ExceptionApplicationController; // Your custom exception

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserModel {

    private final AuthService authService;
    private final ExecutorService executorService;

    // Observable properties for UI binding
    private final ObjectProperty<UserBean> currentUserProperty = new SimpleObjectProperty<>();
    private final BooleanProperty loggedInProperty = new SimpleBooleanProperty(false);
    private final StringProperty usernameDisplayProperty = new SimpleStringProperty(""); // For UI display
    private final StringProperty authStatusMessage = new SimpleStringProperty(""); // For login/registration feedback
    private final StringProperty authErrorMessage = new SimpleStringProperty(""); // For auth errors

    public UserModel(AuthService authService) {
        this.authService = authService;
        this.executorService = Executors.newSingleThreadExecutor();

        // Bind loggedInProperty and usernameDisplayProperty to currentUserProperty
        currentUserProperty.addListener((obs, oldUser, newUser) -> {
            boolean isLoggedIn = newUser != null;
            loggedInProperty.set(isLoggedIn);
            usernameDisplayProperty.set(isLoggedIn ? newUser.getUsername() : "");
            authStatusMessage.set(isLoggedIn ? "Logged in as " + newUser.getUsername() : "");
            authErrorMessage.set(null); // Clear errors on user status change
        });
    }

    // --- Public Methods for Controller to Call (Initiate Actions) ---

    public void login(String username, String password) {
        authStatusMessage.set("Attempting login...");
        authErrorMessage.set(null); // Clear previous errors

        executorService.submit(() -> {
            try {
                boolean success = authService.login(username, password);
                Platform.runLater(() -> {
                    if (success) {
                        // Get the current user from AuthService and set it in our observable property
                        currentUserProperty.set(authService.getCurrentUser());
                        authStatusMessage.set("Login successful!");
                    } else {
                        // On failed login, ensure currentUserProperty is null
                        currentUserProperty.set(null);
                        authStatusMessage.set("Login failed. Invalid username or password.");
                        authErrorMessage.set("Invalid credentials.");
                    }
                });
            } catch (ExceptionApplicationController e) {
                Platform.runLater(() -> {
                    currentUserProperty.set(null);
                    authStatusMessage.set("Login failed.");
                    authErrorMessage.set(e.getMessage());
                });
            } catch (Exception e) {
                // Catch unexpected exceptions
                Platform.runLater(() -> {
                    currentUserProperty.set(null);
                    authStatusMessage.set("An unexpected error occurred during login.");
                    authErrorMessage.set("Error: " + e.getMessage());
                    System.err.println("Login error: " + e.getMessage()); // Log for debugging
                });
            }
        });
    }

    public void register(UserBean newUserBean) {
        authStatusMessage.set("Attempting registration...");
        authErrorMessage.set(null);

        executorService.submit(() -> {
            try {
                boolean success = authService.registerUser(newUserBean);
                Platform.runLater(() -> {
                    if (success) {
                        authStatusMessage.set("Registration successful! You can now log in.");
                        authErrorMessage.set(null);
                        // Optionally auto-login, or just clear form fields
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
                    System.err.println("Registration error: " + e.getMessage()); // Log for debugging
                });
            }
        });
    }

    public void logout() {
        executorService.submit(() -> {
            try {
                authService.logout(); // Delegate actual logout to AuthService
                Platform.runLater(() -> {
                    currentUserProperty.set(null); // Clear the observable user
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

    // --- Observable Property Getters (for UI Binding) ---

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

    // --- Lifecycle Methods ---

    public void shutdown() {
        executorService.shutdownNow(); // Attempt to stop all running tasks
    }
}