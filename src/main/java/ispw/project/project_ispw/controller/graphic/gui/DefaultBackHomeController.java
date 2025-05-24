package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultBackHomeController implements NavigableController {

    private static final Logger LOGGER = Logger.getLogger(DefaultBackHomeController.class.getName());

    @FXML
    private Button backButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button searchButton;

    @FXML
    private Button userButton;

    @FXML
    private TextField searchBar;

    @FXML
    private ComboBox<String> categoryComboBox;

    // Injected GraphicControllerGui instance
    private GraphicControllerGui graphicControllerGui;

    // This method is called by GraphicControllerGui to inject itself
    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;
        // Call setup methods AFTER graphicControllerGui is set, as they depend on it
        setupCategoryComboBox();
        // The other setup methods will be called by FXML's onAction now.
        // We still need to call setupUserButton() here to initialize its text based on current user.
        setupUserButtonText();
    }

    @FXML
    private void initialize() {
        // Initialize should ideally be for FXML elements that don't depend on injected GraphicControllerGui
        // For action handlers, linking them in FXML with onAction is often cleaner.
    }

    // Set up the category combo box
    private void setupCategoryComboBox() {
        categoryComboBox.setItems(FXCollections.observableArrayList("Anime", "Movie", "TV Series"));
        categoryComboBox.getSelectionModel().selectFirst(); // Select a default category
    }

    // This method is called by GraphicControllerGui after injection or when user status might change
    public void setupUserButtonText() {
        // Corrected: Use getCurrentUserBean() as per the refactored ApplicationController
        UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
        if (currentUser != null) {
            userButton.setText(currentUser.getUsername()); // Display username if logged in
        } else {
            userButton.setText("Log In"); // Default text if user is not logged in
        }
    }

    // --- FXML Action Methods ---

    @FXML // Linked directly in FXML: onAction="#handleBackButtonAction"
    private void handleBackButtonAction() {
        LOGGER.log(Level.INFO, "Back button clicked!");
        graphicControllerGui.goBack(); // Delegate to GraphicControllerGui
    }

    @FXML // Linked directly in FXML: onAction="#handleHomeButtonAction"
    private void handleHomeButtonAction() {
        LOGGER.log(Level.INFO, "Home button clicked!");
        graphicControllerGui.setScreen("home"); // Delegate to GraphicControllerGui
    }

    @FXML // Linked directly in FXML: onAction="#handleSearchButtonAction"
    private void handleSearchButtonAction() throws ExceptionApplicationController {
        String searchText = searchBar.getText().trim();
        String selectedCategory = categoryComboBox.getValue();
        LOGGER.log(Level.INFO, "Search button clicked! Search text: {0}, Category: {1}", new Object[]{searchText, selectedCategory});

        if (selectedCategory != null && !searchText.isEmpty()) {
            // Delegate the search action to GraphicControllerGui, which handles navigation and data passing
            graphicControllerGui.performSearchAndNavigate(selectedCategory, searchText);
        } else {
            LOGGER.log(Level.WARNING, "Search text or category is missing for search.");
            showAlert(Alert.AlertType.WARNING, "Search Input", "Please enter search text and select a category.");
        }
    }

    @FXML // Linked directly in FXML: onAction="#handleUserButtonAction"
    private void handleUserButtonAction() {
        LOGGER.log(Level.INFO, "User button clicked!");
        try {
            // Corrected: Use getCurrentUserBean() as per the refactored ApplicationController
            if (graphicControllerGui.getApplicationController().getCurrentUserBean() != null) {
                handleLogout();  // If user is logged in, log out
            } else {
                handleLogin();   // If user is not logged in, redirect to login screen
            }
        } catch (Exception e) { // Catching generic exception for UI interaction
            LOGGER.log(Level.SEVERE, "Error handling user button click.", e);
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
        }
    }

    // --- Helper Methods (internal logic for this controller) ---

    // Handle login action (user is not logged in)
    private void handleLogin() {
        LOGGER.log(Level.INFO, "User not logged in, redirecting to Log In.");
        graphicControllerGui.setScreen("logIn");
    }

    // Handle logout action (user is logged in)
    private void handleLogout() {
        try {
            // Clear the current user in the ApplicationController
            graphicControllerGui.getApplicationController().logout();

            // Update the user button text to "Log In" and redirect to login screen
            setupUserButtonText(); // Re-evaluate button text based on new user status (null)
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

    // Helper method to show alert messages
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}