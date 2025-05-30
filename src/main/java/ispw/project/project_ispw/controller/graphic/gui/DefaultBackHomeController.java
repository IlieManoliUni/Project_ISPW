package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.model.UserModel;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javafx.beans.binding.Bindings; // For general static binding methods, good to keep
import javafx.beans.binding.BooleanBinding; // <-- CRUCIAL: To ensure 'isEmpty()' is seen as a BooleanBinding
import javafx.beans.binding.StringBinding;   // <-- CRUCIAL: To define the result of the conditional binding


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
    private UserModel userModel;

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;

        // Step 1: Explicitly define the BooleanBinding for the condition
        // This ensures the compiler correctly sees userNotLoggedIn as a BooleanBinding
        BooleanBinding userNotLoggedIn = userModel.usernameDisplayProperty().isEmpty();

        // Step 2: Create the conditional StringBinding using the explicitly typed BooleanBinding
        StringBinding userButtonTextBinding = Bindings.when(userNotLoggedIn)
                .then("Log In")
                .otherwise(userModel.usernameDisplayProperty());

        // Step 3: Bind the button's text property to the created StringBinding
        userButton.textProperty().bind(userButtonTextBinding);


        userModel.loggedInProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && oldValue) { // If user was logged in and now is not
                graphicControllerGui.setScreen("logIn");
                showAlert(Alert.AlertType.INFORMATION, "Logout", "You have been successfully logged out.");
            }
        });
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;
    }

    @FXML
    private void initialize() {
        setupCategoryComboBox();
    }

    private void setupCategoryComboBox() {
        categoryComboBox.setItems(FXCollections.observableArrayList("Anime", "Movie", "TvSeries"));
        categoryComboBox.getSelectionModel().selectFirst();
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
        if (userModel.loggedInProperty().get()) {
            userModel.logout();
        } else {
            graphicControllerGui.setScreen("logIn");
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