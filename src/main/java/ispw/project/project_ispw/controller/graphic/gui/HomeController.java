package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.model.UserModel;

import javafx.beans.value.ChangeListener;
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
    private ChangeListener<Boolean> loggedInListener;

    @FXML
    private ListView<ListModel> listView;

    @FXML
    private TextField textField;

    @FXML
    private Button createButton;

    private final ObservableList<ListModel> items = FXCollections.observableArrayList();

    private GraphicControllerGui graphicControllerGui;
    private UserModel userModel;

    @FXML
    private HBox headerInclude;

    @FXML
    private DefaultController headerIncludeController;

    public HomeController() {
        // Default Constructor
    }

    @FXML
    private void initialize() {
        // No elements to initialize
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        if (headerIncludeController != null) {
            headerIncludeController.setGraphicController(this.graphicControllerGui);
        }

        //list of user
        listView.setItems(items);
        listView.setCellFactory(param -> new CustomListCell());

        if (createButton != null) {
            createButton.setOnAction(event -> handleCreateButton());
        }
    }

    @Override
    public void setUserModel(UserModel userModel) {
        if (this.userModel != null && loggedInListener != null) {
            this.userModel.loggedInProperty().removeListener(loggedInListener);
        }

        this.userModel = userModel;

        if (headerIncludeController != null) {
            headerIncludeController.setUserModel(this.userModel);
        }

        if (loggedInListener == null) {
            loggedInListener = (obs, oldVal, newVal) -> {
                LOGGER.log(Level.INFO, "HomeController Listener: loggedInProperty changed: old={0}, new={1}", new Object[]{oldVal, newVal});
                if (newVal.booleanValue()) {
                    LOGGER.log(Level.INFO, "HomeController Listener: User is now logged in, calling loadUserLists.");
                    loadUserLists();
                } else {
                    LOGGER.log(Level.INFO, "HomeController Listener: User is NOT logged in, clearing lists.");
                    items.clear();
                }
            };
            this.userModel.loggedInProperty().addListener(loggedInListener);
        }

        if (userModel.loggedInProperty().get()) {
            LOGGER.log(Level.INFO, "HomeController.setUserModel(): Initial check: User IS logged in. Calling loadUserLists.");
            loadUserLists();
        } else {
            LOGGER.log(Level.INFO, "HomeController.setUserModel(): Initial check: User IS NOT logged in. Clearing lists. No alert/redirect.");
            items.clear();
        }
    }

    private void loadUserLists() {
        items.clear();

        if (userModel == null || !userModel.loggedInProperty().get() || userModel.currentUserProperty().get() == null) {
            LOGGER.log(Level.WARNING, "loadUserLists: User not logged in, clearing lists.");
            return;
        }

        try {
            UserBean currentUser = userModel.currentUserProperty().get();
            List<ListBean> lists = graphicControllerGui.getApplicationController().getListsForUser(currentUser);

            items.setAll(lists.stream()
                    .map(ListModel::new)
                    .toList());
            LOGGER.log(Level.INFO, "loadUserLists: Successfully loaded {0} lists.", items.size());
        } catch (ExceptionApplication e) {
            LOGGER.log(Level.SEVERE, "Error loading lists: {0}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error Loading Lists", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred while loading lists: {0}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred while loading lists: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateButton() {
        String newItemName = textField.getText().trim();
        if (newItemName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a valid list name.");
            return;
        }

        if (userModel == null || !userModel.loggedInProperty().get() || userModel.currentUserProperty().get() == null) {
            showAlert(Alert.AlertType.ERROR, "Error Log", "You must be logged in to create a list.");
            graphicControllerGui.setScreen(SCREEN_LOGIN);
            return;
        }

        try {
            UserBean currentUser = userModel.currentUserProperty().get();
            ListBean newListBean = new ListBean(0, newItemName, currentUser.getUsername());
            graphicControllerGui.getApplicationController().createList(newListBean, currentUser);
            textField.clear();
            refreshListView();
            showAlert(Alert.AlertType.INFORMATION, "Success", "List '" + newItemName + "' created.");
        } catch (ExceptionApplication e) {
            showAlert(Alert.AlertType.ERROR, "List Creation Failed", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred while creating the list: " + e.getMessage());
        }
    }

    private void refreshListView() {
        loadUserLists();
    }

    private class CustomListCell extends ListCell<ListModel> {
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
        protected void updateItem(ListModel item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                text.setText(item.getName());

                setGraphic(hbox);
            }
        }

        private void setupButtonActions() {
            seeButton.setOnAction(event -> navigateToScreen(getItem(), "list"));
            statsButton.setOnAction(event -> navigateToScreen(getItem(), "stats"));
            deleteButton.setOnAction(event -> handleDeleteAction(getItem().getListBean()));
        }


        private void navigateToScreen(ListModel selectedListModel, String screen) {
            if (selectedListModel == null) return;

            if (userModel == null || !userModel.loggedInProperty().get() || userModel.currentUserProperty().get() == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to view lists.");
                graphicControllerGui.setScreen(SCREEN_LOGIN);
                return;
            }

            try {
                graphicControllerGui.navigateToListDetail(selectedListModel, screen);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred during navigation: " + e.getMessage());
            }
        }

        private void handleDeleteAction(ListBean listToDeleteBean) {
            if (listToDeleteBean == null) return;

            if (userModel == null || !userModel.loggedInProperty().get() || userModel.currentUserProperty().get() == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to delete lists.");
                graphicControllerGui.setScreen(SCREEN_LOGIN);
                return;
            }

            try {
                graphicControllerGui.getApplicationController().deleteList(listToDeleteBean);

                items.removeIf(model -> model.getListBean().equals(listToDeleteBean));

                showAlert(Alert.AlertType.INFORMATION, "Success", "List '" + listToDeleteBean.getName() + "' deleted.");
            } catch (ExceptionApplication e) {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred while deleting the list: " + e.getMessage());
            }
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