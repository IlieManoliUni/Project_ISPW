package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.model.UserModel; // <-- NEW: Import UserModel
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.beans.binding.Bindings;

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
    private UserModel userModel;

    @FXML
    private void initialize() {
        // No elements to initialize
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        setupCategoryComboBox();
        setupSearchButton();

    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
        setupUserButtonBindingAndAction();
    }

    private void setupCategoryComboBox() {
        if (categoryComboBox != null) {
            categoryComboBox.setItems(FXCollections.observableArrayList("Anime", "Movie", "TvSeries"));
            categoryComboBox.getSelectionModel().selectFirst();
        }
    }

    private void setupUserButtonBindingAndAction() {
        if (userButton == null || userModel == null) {
            if (userButton != null) {
                userButton.setText("Log In (Loading...)"); // Temporary text
            }
            return;
        }

        userButton.textProperty().bind(Bindings.createStringBinding(() -> {
            String username = userModel.usernameDisplayProperty().get();
            return username.isEmpty() ? "Log In" : username;
        }, userModel.usernameDisplayProperty()));

        userButton.setOnAction(event -> {
            if (userModel.loggedInProperty().get()) {
                handleLogout();
            } else {
                handleLoginClick();
            }
        });
    }

    private void handleLoginClick() {
        graphicControllerGui.setScreen("logIn");
    }

    private void handleLogout() {
        if (userModel == null) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "User model not initialized for logout.");
            return;
        }
        userModel.logout();

        graphicControllerGui.setScreen("logIn");
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
        } catch (Exception e) {
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