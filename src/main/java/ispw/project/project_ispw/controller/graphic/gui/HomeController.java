package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.model.UserModel;

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
import ispw.project.project_ispw.model.ListModel;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeController implements NavigableController, UserAwareController {

    private static final Logger LOGGER = Logger.getLogger(HomeController.class.getName());
    private static final String SCREEN_LOGIN = "logIn";
    private static final String SYSTEM_ERROR_TITLE = "System Error";

    @FXML
    private ListView<ListModel> listView; // Changed type to ListModel

    @FXML
    private TextField textField;

    @FXML
    private Button createButton;

    private final ObservableList<ListModel> items = FXCollections.observableArrayList(); // Stores ListModel objects

    private GraphicControllerGui graphicControllerGui;
    private UserModel userModel;

    @FXML
    private HBox headerInclude; // Assuming this is fx:id="headerInclude" in your home.fxml

    @FXML
    private DefaultController headerIncludeController; // Correctly typed for DefaultController as the header's controller

    public HomeController() {
        // Empty constructor
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        // Pass the GraphicController to the included header controller
        if (headerIncludeController != null) {
            headerIncludeController.setGraphicController(this.graphicControllerGui);
        }

        // Set the ObservableList as the data source for the ListView
        listView.setItems(items);
        // Define how each ListModel object should be rendered in the ListView
        listView.setCellFactory(param -> new CustomListCell());

        // Set action for the create button
        if (createButton != null) {
            createButton.setOnAction(event -> handleCreateButton());
        }
    }

    @FXML
    private void initialize() {
        // FXML initialization logic goes here.
        // Currently, no explicit FXML elements need initialization here,
        // as they are handled by FXCollections.observableArrayList() or setters.
    }

    @Override
    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;

        // Pass the UserModel to the included header controller so it can react to user state
        if (headerIncludeController != null) {
            headerIncludeController.setUserModel(this.userModel);
        }

        // Add a listener to react to changes in the user's login status.
        // This ensures the UI reflects login/logout events dynamically.
        userModel.loggedInProperty().addListener((obs, oldVal, newVal) -> {
            LOGGER.log(Level.INFO, "HomeController Listener: loggedInProperty changed: old={0}, new={1}", new Object[]{oldVal, newVal});
            if (newVal.booleanValue()) { // User just logged in or is already logged in
                LOGGER.log(Level.INFO, "HomeController Listener: User is now logged in, calling loadUserLists.");
                loadUserLists();
            } else { // User just logged out or is no longer logged in
                LOGGER.log(Level.INFO, "HomeController Listener: User is NOT logged in, clearing lists and showing logout alert.");
                items.clear(); // Clear displayed lists when logged out
                // Only show a logout alert if the user was previously logged in and actively logged out.
                // This avoids showing an alert when the app starts and no user is logged in.
                if (oldVal.booleanValue()) {
                    showAlert(Alert.AlertType.INFORMATION, "Logged Out", "You have been successfully logged out. Your lists have been cleared.");
                }
                // Important: Do NOT redirect to the login screen here. The GraphicControllerGui's
                // logout method (called via UserModel) should handle the screen redirection.
                // If a user simply lands on this screen while not logged in, the lists will just be empty.
            }
        });

        // Handle the initial state when HomeController is first set up.
        // This checks if the user is already logged in when the view loads.
        if (userModel.loggedInProperty().get()) {
            LOGGER.log(Level.INFO, "HomeController.setUserModel(): Initial check: User IS logged in. Calling loadUserLists.");
            loadUserLists();
        } else {
            LOGGER.log(Level.INFO, "HomeController.setUserModel(): Initial check: User IS NOT logged in. Clearing lists. No alert/redirect.");
            items.clear();
            // Removed previous logic that might have shown an alert or redirected here.
            // This prevents a potential race condition or unnecessary alerts on startup.
        }
    }

    /**
     * Loads the current user's lists from the application backend and updates the ListView.
     * Ensures the user is logged in before attempting to fetch data.
     */
    private void loadUserLists() {
        items.clear(); // Always clear existing items before loading new ones

        // Pre-check for login status. If not logged in, show an alert and redirect to login.
        if (userModel == null || !userModel.loggedInProperty().get() || userModel.currentUserProperty().get() == null) {
            LOGGER.log(Level.WARNING, "loadUserLists: User not logged in, showing alert and redirecting.");
            showAlert(Alert.AlertType.INFORMATION, "Not Logged In", "Please log in to view your lists.");
            graphicControllerGui.setScreen(SCREEN_LOGIN);
            return;
        }

        try {
            UserBean currentUser = userModel.currentUserProperty().get();
            List<ListBean> lists = graphicControllerGui.getApplicationController().getListsForUser(currentUser);
            // --- CRITICAL CHANGE: Map ListBean to ListModel ---
            items.setAll(lists.stream()
                    .map(ListModel::new) // Create a new ListModel for each ListBean
                    .toList()); // Changed from .collect(Collectors.toList())
            // --- END CRITICAL CHANGE ---
            LOGGER.log(Level.INFO, "loadUserLists: Successfully loaded {0} lists.", items.size());
        } catch (ExceptionApplicationController e) {
            LOGGER.log(Level.SEVERE, "Error loading lists: {0}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error Loading Lists", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred while loading lists: {0}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred while loading lists: " + e.getMessage());
        }
    }

    /**
     * Handles the action when the 'Create List' button is clicked.
     * Validates input, creates a new list via the application controller, and refreshes the view.
     */
    @FXML
    private void handleCreateButton() {
        String newItemName = textField.getText().trim();
        if (newItemName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a valid list name.");
            return;
        }

        // Ensure user is logged in before allowing list creation
        if (userModel == null || !userModel.loggedInProperty().get() || userModel.currentUserProperty().get() == null) {
            showAlert(Alert.AlertType.ERROR, "Error Log", "You must be logged in to create a list.");
            graphicControllerGui.setScreen(SCREEN_LOGIN);
            return;
        }

        try {
            UserBean currentUser = userModel.currentUserProperty().get();
            // Create a new ListBean with initial data
            ListBean newListBean = new ListBean(0, newItemName, currentUser.getUsername());
            graphicControllerGui.getApplicationController().createList(newListBean, currentUser);
            textField.clear(); // Clear the input field after successful creation
            refreshListView(); // Reload lists to show the newly created one
            showAlert(Alert.AlertType.INFORMATION, "Success", "List '" + newItemName + "' created.");
        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "List Creation Failed", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred while creating the list: " + e.getMessage());
        }
    }

    /**
     * Refreshes the ListView by reloading the user's lists.
     */
    private void refreshListView() {
        loadUserLists();
    }

    /**
     * Custom ListCell implementation to display ListModel objects in the ListView.
     * It provides buttons for 'See', 'Stats', and 'Delete' for each list item.
     */
    private class CustomListCell extends ListCell<ListModel> { // ListCell is now parameterized with ListModel
        private final HBox hbox;
        private final Text text;
        private final Button seeButton;
        private final Button statsButton;
        private final Button deleteButton;
        private final Region spacer;

        public CustomListCell() {
            hbox = new HBox(10); // Spacing between elements
            text = new Text();
            seeButton = new Button("See");
            statsButton = new Button("Stats");
            deleteButton = new Button("Delete");
            spacer = new Region(); // Spacer to push buttons to the right

            HBox.setHgrow(spacer, Priority.ALWAYS); // Make the spacer grow to fill available space
            hbox.getChildren().addAll(text, spacer, seeButton, statsButton, deleteButton);

            setupButtonActions();
        }

        @Override
        protected void updateItem(ListModel item, boolean empty) { // updateItem now receives a ListModel
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null); // No item, no graphic
            } else {
                // Display the name from the ListModel. You could also bind to item.displayStringProperty()
                // for a more complex string like "List Name (X items)".
                text.setText(item.getName());
                // Alternatively, for a reactive display string:

                setGraphic(hbox); // Set the HBox as the cell's graphic
            }
        }

        /**
         * Sets up the action listeners for the buttons within each list cell.
         */
        private void setupButtonActions() {
            // FIX: Pass the ListModel directly to navigateToScreen
            seeButton.setOnAction(event -> navigateToScreen(getItem(), "list"));
            statsButton.setOnAction(event -> navigateToScreen(getItem(), "stats"));
            // For delete, still pass ListBean as ApplicationController expects ListBean
            deleteButton.setOnAction(event -> handleDeleteAction(getItem().getListBean()));
        }

        /**
         * Navigates to a specific screen (e.g., list details or stats) for the selected list.
         *
         * @param selectedListModel The ListModel object for the selected list. (CHANGED TYPE)
         * @param screen The target screen (e.g., "list" for detail, "stats" for statistics).
         */
        private void navigateToScreen(ListModel selectedListModel, String screen) { // CHANGED TYPE
            if (selectedListModel == null) return; // Should not happen if called correctly

            // Re-check login status before navigation for critical operations
            if (userModel == null || !userModel.loggedInProperty().get() || userModel.currentUserProperty().get() == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to view lists.");
                graphicControllerGui.setScreen(SCREEN_LOGIN);
                return;
            }

            try {
                // Now pass the ListModel to graphicControllerGui.navigateToListDetail
                graphicControllerGui.navigateToListDetail(selectedListModel, screen);
            } catch (ExceptionApplicationController e) {
                showAlert(Alert.AlertType.ERROR, "Navigation Error", e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred during navigation: " + e.getMessage());
            }
        }

        /**
         * Handles the action to delete a selected list.
         *
         * @param listToDeleteBean The ListBean object representing the list to be deleted (extracted from ListModel).
         */
        private void handleDeleteAction(ListBean listToDeleteBean) {
            if (listToDeleteBean == null) return; // Should not happen if called correctly

            // Re-check login status before deletion for security
            if (userModel == null || !userModel.loggedInProperty().get() || userModel.currentUserProperty().get() == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to delete lists.");
                graphicControllerGui.setScreen(SCREEN_LOGIN);
                return;
            }

            try {
                graphicControllerGui.getApplicationController().deleteList(listToDeleteBean);

                // IMPORTANT: To remove the item from the ListView, you need to find the specific ListModel
                // instance that wraps this listToDeleteBean.
                // A common way to do this is to iterate or stream through `items`
                // and compare the underlying ListBean.
                items.removeIf(model -> model.getListBean().equals(listToDeleteBean));

                showAlert(Alert.AlertType.INFORMATION, "Success", "List '" + listToDeleteBean.getName() + "' deleted.");
            } catch (ExceptionApplicationController e) {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred while deleting the list: " + e.getMessage());
            }
        }
    }

    /**
     * Displays a JavaFX Alert dialog to the user.
     * @param alertType The type of alert (e.g., WARNING, ERROR, INFORMATION).
     * @param title The title of the alert dialog.
     * @param message The content message of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}