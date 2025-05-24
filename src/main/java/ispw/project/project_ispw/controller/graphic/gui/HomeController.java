package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.ListBean; // Use ListBean from shared bean package
import ispw.project.project_ispw.bean.UserBean;   // Use UserBean from shared bean package
import ispw.project.project_ispw.exception.ExceptionApplicationController; // Corrected import
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeController implements NavigableController {

    private static final Logger LOGGER = Logger.getLogger(HomeController.class.getName());

    @FXML
    private ListView<String> listView;

    @FXML
    private TextField textField;

    private final ObservableList<String> items = FXCollections.observableArrayList();

    // Injected GraphicControllerGui instance
    private GraphicControllerGui graphicControllerGui;

    // Default constructor (no need for IOException anymore as getInstance is gone)
    public HomeController() {
        // Empty constructor
    }

    // This method is called by GraphicControllerGui to inject itself
    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;
        // Now that graphicControllerGui is set, load the lists
        loadUserLists();
        listView.setItems(items);
        listView.setCellFactory(param -> new CustomListCell());
    }

    @FXML
    private void initialize() {
        // FXML initialization, but no logic dependent on graphicControllerGui here.
    }

    /**
     * Loads the current user's lists by delegating to the ApplicationController.
     */
    private void loadUserLists() {
        items.clear(); // Clear existing items before loading
        try {
            // Corrected: Use getCurrentUserBean() as per the refactored ApplicationController
            UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
            if (currentUser != null) {
                // Delegate to ApplicationController to get user lists
                List<ListBean> lists = graphicControllerGui.getApplicationController().getListsForUser(currentUser);
                items.setAll(lists.stream().map(ListBean::getName).toList());
            } else {
                LOGGER.log(Level.INFO, "No current user set when attempting to load lists for Home screen.");
                // Optionally, show a message to the user that they need to log in
                showAlert(Alert.AlertType.INFORMATION, "Not Logged In", "Please log in to view your lists.");
                graphicControllerGui.setScreen("logIn"); // Redirect to login if not logged in
            }
        } catch (ExceptionApplicationController e) {
            LOGGER.log(Level.SEVERE, "Application error loading user lists: " + e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Error Loading Lists", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error loading user lists.", e);
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
            // Corrected: Use getCurrentUserBean() as per the refactored ApplicationController
            UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
            if (currentUser != null) {
                // Delegate to ApplicationController to create the list
                ListBean newListBean = new ListBean(0, newItemName, currentUser.getUsername()); // ID will be set by DAO
                // FIX: Pass currentUser as the second argument
                graphicControllerGui.getApplicationController().createList(newListBean, currentUser);
                textField.clear();
                refreshListView(); // Refresh the UI after creation
                showAlert(Alert.AlertType.INFORMATION, "Success", "List '" + newItemName + "' created.");
            } else {
                LOGGER.log(Level.WARNING, "Attempted to create list without a current user.");
                showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to create a list.");
                graphicControllerGui.setScreen("logIn"); // Redirect to login if somehow not logged in
            }
        } catch (ExceptionApplicationController e) {
            LOGGER.log(Level.SEVERE, "Application error creating list: " + e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "List Creation Failed", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error creating list.", e);
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

            // Set up button actions directly in the constructor, they use the item available in updateItem
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
            seeButton.setOnAction(event -> navigateToScreen(getItem(), "list")); // getItem() provides the current list name
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
            LOGGER.log(Level.INFO, "{0} button clicked for list: {1}", new Object[]{screen, listName});

            try {
                // Corrected: Use getCurrentUserBean() as per the refactored ApplicationController
                UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
                if (currentUser == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to view lists.");
                    graphicControllerGui.setScreen("logIn");
                    return;
                }

                // Find the ListBean by name and current user
                ListBean selectedList = graphicControllerGui.getApplicationController().findListForUserByName(currentUser, listName);

                if (selectedList != null) {
                    // GraphicControllerGui will handle setting the selected list for the target controller
                    // and navigating to the correct screen.
                    graphicControllerGui.navigateToListDetail(selectedList, screen);
                } else {
                    LOGGER.log(Level.WARNING, "List not found: {0} for user {1}", new Object[]{listName, currentUser.getUsername()});
                    showAlert(Alert.AlertType.ERROR, "List Not Found", "The selected list could not be found.");
                }
            } catch (ExceptionApplicationController e) {
                LOGGER.log(Level.SEVERE, "Application error during navigation to {0} for list {1}: {2}", new Object[]{screen, listName, e.getMessage()});
                showAlert(Alert.AlertType.ERROR, "Navigation Error", e.getMessage());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected error during navigation to {0} for list {1}.", new Object[]{screen, listName, e});
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
            LOGGER.log(Level.INFO, "Delete button clicked for list: {0}", listName);

            try {
                // Corrected: Use getCurrentUserBean() as per the refactored ApplicationController
                UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
                if (currentUser == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to delete lists.");
                    graphicControllerGui.setScreen("logIn");
                    return;
                }

                ListBean listToDelete = graphicControllerGui.getApplicationController().findListForUserByName(currentUser, listName);

                if (listToDelete != null) {
                    // Delegate to ApplicationController to delete the list
                    graphicControllerGui.getApplicationController().deleteList(listToDelete);
                    getListView().getItems().remove(listName); // Remove from UI
                    showAlert(Alert.AlertType.INFORMATION, "Success", "List '" + listName + "' deleted.");
                } else {
                    LOGGER.log(Level.WARNING, "Attempted to delete non-existent list: {0}", listName);
                    showAlert(Alert.AlertType.WARNING, "List Not Found", "The list to delete was not found.");
                }
            } catch (ExceptionApplicationController e) {
                LOGGER.log(Level.SEVERE, "Application error deleting list: " + listName + ": " + e.getMessage(), e);
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", e.getMessage());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected error deleting list: " + listName, e);
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