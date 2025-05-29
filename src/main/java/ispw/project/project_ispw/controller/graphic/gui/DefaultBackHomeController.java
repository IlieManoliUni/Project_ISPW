package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class DefaultBackHomeController implements NavigableController {

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

    private GraphicControllerGui graphicControllerGui;

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;
        setupCategoryComboBox();
        setupUserButtonText();
    }

    @FXML
    private void initialize() {
        //no elements to initialize
    }

    private void setupCategoryComboBox() {
        categoryComboBox.setItems(FXCollections.observableArrayList("Anime", "Movie", "TvSeries"));
        categoryComboBox.getSelectionModel().selectFirst();
    }

    public void setupUserButtonText() {
        UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
        if (currentUser != null) {
            userButton.setText(currentUser.getUsername());
        } else {
            userButton.setText("Log In");
        }
    }

    @FXML
    private void handleBackButtonAction() {
        graphicControllerGui.goBack();
    }

    @FXML
    private void handleHomeButtonAction() {
        graphicControllerGui.setScreen("home");
    }

    @FXML
    private void handleSearchButtonAction() throws ExceptionApplicationController {
        String searchText = searchBar.getText().trim();
        String selectedCategory = categoryComboBox.getValue();

        if (selectedCategory != null && !searchText.isEmpty()) {
            graphicControllerGui.performSearchAndNavigate(selectedCategory, searchText);
        } else {
            showAlert(Alert.AlertType.WARNING, "Search Input", "Please enter search text and select a category.");
        }
    }

    @FXML
    private void handleUserButtonAction() {
        try {
            if (graphicControllerGui.getApplicationController().getCurrentUserBean() != null) {
                handleLogout();
            } else {
                handleLogin();
            }
        } catch (Exception _) {
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
        }
    }

    private void handleLogin() {
        graphicControllerGui.setScreen("logIn");
    }

    private void handleLogout() {
        try {
            graphicControllerGui.getApplicationController().logout();
            setupUserButtonText();
            graphicControllerGui.setScreen("logIn");
        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "Logout Error", e.getMessage());
        } catch (Exception _) {
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during logout.");
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