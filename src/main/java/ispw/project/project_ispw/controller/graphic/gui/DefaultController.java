package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.model.UserModel; // <-- NEW: Import UserModel
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.beans.binding.Bindings; // <-- NEW: Import Bindings

public class DefaultController implements NavigableController {

    private static final String SYSTEM_ERROR_TITLE = "System Error";

    @FXML
    private TextField searchBar;

    @FXML
    private Button userButton;

    @FXML
    private Button searchButton;

    @FXML
    private ComboBox<String> categoryComboBox;

    private GraphicControllerGui graphicControllerGui;
    private UserModel userModel; // <-- NEW: Declare UserModel instance

    @FXML
    private void initialize() {
        // No elements to initialize
        // Binding and other setup should happen after graphicControllerGui and userModel are set
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        // setupCategoryComboBox() and setupSearchButton() can be called here
        // as they don't depend on userModel
        setupCategoryComboBox();
        setupSearchButton();

        // setupUserButton() is now handled by setUserModel's binding logic.
        // The `setOnAction` for userButton is moved into `setUserModel` or a new setup method
        // called from `setUserModel` to ensure userModel is available.
    }

    // <-- NEW: Method to set the UserModel, called by GraphicControllerGui
    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
        setupUserButtonBindingAndAction(); // Set up button binding and action here
    }

    private void setupCategoryComboBox() {
        if (categoryComboBox != null) {
            categoryComboBox.setItems(FXCollections.observableArrayList("Anime", "Movie", "TvSeries"));
            categoryComboBox.getSelectionModel().selectFirst();
        }
    }

    // <-- MODIFIED: This method now sets up binding and action.
    private void setupUserButtonBindingAndAction() {
        if (userButton == null || userModel == null) {
            // Handle cases where button or model are not yet initialized (e.g., FXML not loaded)
            if (userButton != null) {
                userButton.setText("Log In (Loading...)"); // Temporary text
            }
            return;
        }

        // Bind the userButton's text to the usernameDisplayProperty
        // If usernameDisplayProperty is empty, display "Log In". Otherwise, display the username.
        userButton.textProperty().bind(Bindings.createStringBinding(() -> {
            String username = userModel.usernameDisplayProperty().get();
            return username.isEmpty() ? "Log In" : username;
        }, userModel.usernameDisplayProperty()));

        // Set the action for the button based on login state
        userButton.setOnAction(event -> {
            if (userModel.loggedInProperty().get()) { // Check loggedInProperty from UserModel
                handleLogout();
            } else {
                handleLoginClick();
            }
        });
    }

    private void handleLoginClick() {
        // Navigate to the login screen
        graphicControllerGui.setScreen("logIn");
    }

    private void handleLogout() {
        if (userModel == null) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "User model not initialized for logout.");
            return;
        }
        // Call logout on the UserModel. The UI will update automatically via binding.
        userModel.logout();

        // After logout, navigate to the login screen or home
        graphicControllerGui.setScreen("logIn"); // Or "home" depending on desired flow
    }

    private void setupSearchButton() {
        if (searchButton == null) {
            return;
        }

        searchButton.setOnAction(event -> handleSearchButtonClick());
    }

    private void handleSearchButtonClick() {
        if (searchBar == null || categoryComboBox == null || graphicControllerGui == null) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "UI components or controller not initialized for search.");
            return;
        }

        String searchText = searchBar.getText().trim();
        String selectedCategory = categoryComboBox.getValue();

        if (selectedCategory == null || searchText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Search Input", "Please enter search text and select a category.");
            return;
        }

        try {
            graphicControllerGui.performSearchAndNavigate(selectedCategory, searchText);
        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "Search Error", e.getMessage());
        } catch (Exception e) { // Catch generic Exception
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred during search: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}