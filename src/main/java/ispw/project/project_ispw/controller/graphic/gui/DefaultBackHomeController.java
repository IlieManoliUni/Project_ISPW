package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.model.UserModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;


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

    private ChangeListener<Boolean> loggedInListener;

    public void setUserModel(UserModel userModel) {
        if (this.userModel != null && loggedInListener != null) {
            this.userModel.loggedInProperty().removeListener(loggedInListener);
        }

        this.userModel = userModel;

        BooleanBinding userNotLoggedIn = userModel.usernameDisplayProperty().isEmpty();

        StringBinding userButtonTextBinding = Bindings.when(userNotLoggedIn)
                .then("Log In")
                .otherwise(userModel.usernameDisplayProperty());

        userButton.textProperty().bind(userButtonTextBinding);

        if (loggedInListener == null) {
            loggedInListener = (observable, oldValue, newValue) -> {
                // This listener ensures the userButton text is updated automatically thanks to the binding.
            };
            this.userModel.loggedInProperty().addListener(loggedInListener);
        }
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
    private void handleSearchButtonAction() {
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