package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class DefaultController implements NavigableController {

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

    // This method is called by the FXMLLoader after all @FXML fields are injected.
    // It runs BEFORE setGraphicController().
    @FXML
    private void initialize() {
        // FXML initialization. Do not place logic here that depends on 'graphicControllerGui' being set.
    }

    // This method is called by HomeController (or GraphicControllerGui if loaded directly)
    // to inject the main GraphicControllerGui instance.
    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        // Call setup methods AFTER graphicControllerGui is set, as they depend on it
        setupCategoryComboBox();
        setupUserButton();
        setupSearchButton();
    }

    // Set up the category combo box
    private void setupCategoryComboBox() {
        if (categoryComboBox != null) { // Defensive check
            categoryComboBox.setItems(FXCollections.observableArrayList("Anime", "Movie", "TvSeries"));
            categoryComboBox.getSelectionModel().selectFirst(); // Select a default category
        }
    }

    // Set up the user button action and display logged-in username
    private void setupUserButton() {
        if (userButton != null) { // Defensive check
            if (graphicControllerGui != null && graphicControllerGui.getApplicationController() != null) {
                UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
                if (currentUser != null) {
                    userButton.setText(currentUser.getUsername()); // Display username
                } else {
                    userButton.setText("Log In"); // Default text if user is not logged in
                }

                userButton.setOnAction(event -> {
                    try {
                        if (graphicControllerGui.getApplicationController().getCurrentUserBean() != null) {
                            handleLogout();  // If user is logged in, log out
                        } else {
                            handleLoginClick(); // If user is not logged in, redirect to login screen
                        }
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
                    }
                });
            } else {
                userButton.setText("Log In (Error)"); // Indicate an error state
            }
        }
    }

    // Handle login click (if user is not logged in)
    private void handleLoginClick() {
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
            graphicControllerGui.setScreen("logIn");
        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "Logout Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during logout.");
        }
    }

    // Set up the search button action
    private void setupSearchButton() {
        if (searchButton != null) { // Defensive check
            searchButton.setOnAction(event -> {
                String searchText = (searchBar != null) ? searchBar.getText().trim() : ""; // Defensive check for searchBar
                String selectedCategory = (categoryComboBox != null) ? categoryComboBox.getValue() : null; // Defensive check for categoryComboBox

                if (selectedCategory != null && !searchText.isEmpty()) {
                    try {
                        // Delegate the search action to GraphicControllerGui, which handles navigation and data passing
                        graphicControllerGui.performSearchAndNavigate(selectedCategory, searchText);
                    } catch (ExceptionApplicationController e) {
                        showAlert(Alert.AlertType.ERROR, "Search Error", e.getMessage());
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during search.");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Search Input", "Please enter search text and select a category.");
                }
            });
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