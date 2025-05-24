package ispw.project.project_ispw.controller.graphic.gui; // Updated package

import ispw.project.project_ispw.controller.application.ApplicationController; // Import ApplicationController
import ispw.project.project_ispw.bean.UserBean; // Use UserBean from shared bean package
import ispw.project.project_ispw.exception.ExceptionApplicationController; // Updated: Import your custom Application Exception
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultController implements NavigableController {

    private static final Logger LOGGER = Logger.getLogger(DefaultController.class.getName());

    @FXML
    private TextField searchBar;

    @FXML
    private Button userButton;

    @FXML
    private Button searchButton;

    @FXML
    private ComboBox<String> categoryComboBox;

    // Injected GraphicControllerGui instance
    private GraphicControllerGui graphicControllerGui;

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;
        // Call setup methods AFTER graphicControllerGui is set, as they depend on it
        setupCategoryComboBox();
        setupUserButton();
        setupSearchButton();
    }

    @FXML
    private void initialize() {
        // Initialize should typically contain FXML-specific setup not dependent on injected dependencies.
        // Moving setup methods to be called after setGraphicController.
    }

    // Set up the category combo box
    private void setupCategoryComboBox() {
        categoryComboBox.setItems(FXCollections.observableArrayList("Anime", "Movie", "TV Series"));
        categoryComboBox.getSelectionModel().selectFirst(); // Select a default category
    }

    // Set up the user button action and display logged-in username
    private void setupUserButton() {
        // Corrected: Use getCurrentUserBean() as per the refactored ApplicationController
        UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
        if (currentUser != null) {
            userButton.setText(currentUser.getUsername()); // Display username
        } else {
            userButton.setText("Log In"); // Default text if user is not logged in
        }

        userButton.setOnAction(event -> {
            LOGGER.log(Level.INFO, "User button clicked!");
            try {
                // Corrected: Use getCurrentUserBean() as per the refactored ApplicationController
                if (graphicControllerGui.getApplicationController().getCurrentUserBean() != null) {
                    handleLogout();  // If user is logged in, log out
                } else {
                    handleLoginClick(); // If user is not logged in, redirect to login screen
                }
            } catch (Exception e) { // Catching generic exception for UI interaction
                LOGGER.log(Level.SEVERE, "Error handling user button click.", e);
                showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
            }
        });
    }

    // Handle login click (if user is not logged in)
    private void handleLoginClick() {
        LOGGER.log(Level.INFO, "User not logged in, redirecting to Log In.");
        graphicControllerGui.setScreen("logIn");
    }

    // Handle logout action
    private void handleLogout() {
        try {
            // Clear the current user in the ApplicationController
            graphicControllerGui.getApplicationController().logout(); // Assuming an ApplicationController.logout() method

            // Update the user button text to "Log In"
            userButton.setText("Log In");

            // Redirect to the login screen
            LOGGER.log(Level.INFO, "User logged out, redirecting to Log In.");
            graphicControllerGui.setScreen("logIn");
        } catch (ExceptionApplicationController e) {
            LOGGER.log(Level.SEVERE, "Application error during logout.", e);
            showAlert(Alert.AlertType.ERROR, "Logout Error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during logout.", e);
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during logout.");
        }
    }

    // Set up the search button action
    private void setupSearchButton() {
        searchButton.setOnAction(event -> {
            String searchText = searchBar.getText().trim();
            String selectedCategory = categoryComboBox.getValue();
            LOGGER.log(Level.INFO, "Search button clicked! Search text: {0}, Category: {1}", new Object[]{searchText, selectedCategory});

            if (selectedCategory != null && !searchText.isEmpty()) {
                // Delegate the search action to GraphicControllerGui, which handles navigation and data passing
                try {
                    graphicControllerGui.performSearchAndNavigate(selectedCategory, searchText);
                } catch (ExceptionApplicationController e) {
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.log(Level.WARNING, "Search text or category is missing for search.");
                showAlert(Alert.AlertType.WARNING, "Search Input", "Please enter search text and select a category.");
            }
        });
    }

    // Helper method to show alert messages
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}