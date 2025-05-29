package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.util.List;

public class HomeController implements NavigableController {

    @FXML
    private ListView<String> listView;

    @FXML
    private TextField textField;

    private final ObservableList<String> items = FXCollections.observableArrayList();

    // Injected GraphicControllerGui instance
    private GraphicControllerGui graphicControllerGui;

    // --- NEW: FXML injection for the included DefaultController ---
    // This field will be automatically populated by FXMLLoader
    // if 'home.fxml' has <fx:include fx:id="headerInclude" .../>
    @FXML
    private DefaultController headerIncludeController;
    // --- END NEW ---

    // Default constructor
    public HomeController() {
        // Empty constructor
    }

    // This method is called by GraphicControllerGui to inject itself
    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        // --- NEW: Manually inject GraphicControllerGui into the DefaultController ---
        // This is crucial because DefaultController is embedded via fx:include
        if (headerIncludeController != null) {
            headerIncludeController.setGraphicController(this.graphicControllerGui);
        }
        // --- END NEW ---

        // Now that graphicControllerGui is set, load the lists
        loadUserLists();
        listView.setItems(items);
        listView.setCellFactory(param -> new CustomListCell());
    }

    @FXML
    private void initialize() {
        // This method is called by FXMLLoader after all @FXML annotated fields are populated.
        // It runs BEFORE setGraphicController().
        // Therefore, do NOT place logic here that depends on 'graphicControllerGui' being set.
    }

    /**
     * Loads the current user's lists by delegating to the ApplicationController.
     */
    private void loadUserLists() {
        items.clear(); // Clear existing items before loading
        try {
            UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
            if (currentUser != null) {
                // Delegate to ApplicationController to get user lists
                List<ListBean> lists = graphicControllerGui.getApplicationController().getListsForUser(currentUser);
                items.setAll(lists.stream().map(ListBean::getName).toList());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Not Logged In", "Please log in to view your lists.");
                graphicControllerGui.setScreen("logIn"); // Redirect to login if not logged in
            }
        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Lists", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while loading lists.");
        }
    }

    /**
     * Handles the creation of a new list.
     * Delegates list creation to the ApplicationController.
     */
    @FXML
    private void handleCreateButton() {
        String newItemName = textField.getText().trim();
        if (newItemName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a valid list name.");
            return;
        }

        try {
            UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
            if (currentUser != null) {
                ListBean newListBean = new ListBean(0, newItemName, currentUser.getUsername());
                graphicControllerGui.getApplicationController().createList(newListBean, currentUser);
                textField.clear();
                refreshListView(); // Refresh the UI after creation
                showAlert(Alert.AlertType.INFORMATION, "Success", "List '" + newItemName + "' created.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to create a list.");
                graphicControllerGui.setScreen("logIn"); // Redirect to login if somehow not logged in
            }
        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "List Creation Failed", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while creating the list.");
        }
    }

    /**
     * Refreshes the list view by clearing and reloading user lists.
     */
    private void refreshListView() {
        loadUserLists();
    }

    /**
     * Custom ListCell implementation for displaying user lists with action buttons.
     */
    private class CustomListCell extends ListCell<String> {
        private final HBox hbox;
        private final Text text;
        private final Button seeButton;
        private final Button statsButton;
        private final Button deleteButton;
        private final Region spacer;

        public CustomListCell() {
            hbox = new HBox(10);
            text = new Text();
            seeButton = new Button("See");
            statsButton = new Button("Stats");
            deleteButton = new Button("Delete");
            spacer = new Region();

            HBox.setHgrow(spacer, Priority.ALWAYS);
            hbox.getChildren().addAll(text, spacer, seeButton, statsButton, deleteButton);

            setupButtonActions();
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                text.setText(item);
                setGraphic(hbox);
            }
        }

        /**
         * Sets up the actions for the buttons within each list cell.
         */
        private void setupButtonActions() {
            seeButton.setOnAction(event -> navigateToScreen(getItem(), "list"));
            statsButton.setOnAction(event -> navigateToScreen(getItem(), "stats"));
            deleteButton.setOnAction(event -> handleDeleteAction(getItem()));
        }

        /**
         * Navigates to a specified screen (list or stats) for the selected list.
         * Delegates data passing and screen transition to GraphicControllerGui.
         * @param listName The name of the list clicked.
         * @param screen The target screen ("list" or "stats").
         */
        private void navigateToScreen(String listName, String screen) {
            if (listName == null) return;

            try {
                UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
                if (currentUser == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to view lists.");
                    graphicControllerGui.setScreen("logIn");
                    return;
                }

                ListBean selectedList = graphicControllerGui.getApplicationController().findListForUserByName(currentUser, listName);

                if (selectedList != null) {
                    graphicControllerGui.navigateToListDetail(selectedList, screen);
                } else {
                    showAlert(Alert.AlertType.ERROR, "List Not Found", "The selected list could not be found.");
                }
            } catch (ExceptionApplicationController e) {
                showAlert(Alert.AlertType.ERROR, "Navigation Error", e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during navigation.");
            }
        }

        /**
         * Handles the deletion of a list.
         * Delegates list deletion to the ApplicationController.
         * @param listName The name of the list to delete.
         */
        private void handleDeleteAction(String listName) {
            if (listName == null) return;

            try {
                UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
                if (currentUser == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to delete lists.");
                    graphicControllerGui.setScreen("logIn");
                    return;
                }

                ListBean listToDelete = graphicControllerGui.getApplicationController().findListForUserByName(currentUser, listName);

                if (listToDelete != null) {
                    graphicControllerGui.getApplicationController().deleteList(listToDelete);
                    getListView().getItems().remove(listName); // Remove from UI
                    showAlert(Alert.AlertType.INFORMATION, "Success", "List '" + listName + "' deleted.");
                } else {
                    showAlert(Alert.AlertType.WARNING, "List Not Found", "The list to delete was not found.");
                }
            } catch (ExceptionApplicationController e) {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while deleting the list.");
            }
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