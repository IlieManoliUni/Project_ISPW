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

    @FXML
    private void initialize() {
        //no elements to initialize
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        setupCategoryComboBox();
        setupUserButton();
        setupSearchButton();
    }

    private void setupCategoryComboBox() {
        if (categoryComboBox != null) {
            categoryComboBox.setItems(FXCollections.observableArrayList("Anime", "Movie", "TvSeries"));
            categoryComboBox.getSelectionModel().selectFirst();
        }
    }

    private void setupUserButton() {
        if (userButton == null) {
            return;
        }

        if (graphicControllerGui == null || graphicControllerGui.getApplicationController() == null) {
            userButton.setText("Log In (Error)");
            return;
        }

        UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
        if (currentUser != null) {
            userButton.setText(currentUser.getUsername());
        } else {
            userButton.setText("Log In");
        }

        userButton.setOnAction(event -> handleUserButtonClick());
    }

    private void handleUserButtonClick() {
        try {
            if (graphicControllerGui.getApplicationController().getCurrentUserBean() != null) {
                handleLogout();
            } else {
                handleLoginClick();
            }
        } catch (Exception _) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred.");
        }
    }

    private void handleLoginClick() {
        graphicControllerGui.setScreen("logIn");
    }

    private void handleLogout() {
        try {
            graphicControllerGui.getApplicationController().logout();
            userButton.setText("Log In");
            graphicControllerGui.setScreen("logIn");
        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "Logout Error", e.getMessage());
        } catch (Exception _) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred during logout.");
        }
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
        } catch (Exception _) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred during search.");
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