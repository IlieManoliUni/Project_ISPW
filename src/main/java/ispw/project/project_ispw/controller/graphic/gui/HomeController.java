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

    private static final String SCREEN_LOGIN = "logIn";

    @FXML
    private ListView<String> listView;

    @FXML
    private TextField textField;

    private final ObservableList<String> items = FXCollections.observableArrayList();

    private GraphicControllerGui graphicControllerGui;

    @FXML
    private DefaultController headerIncludeController;

    public HomeController() {
        // Empty constructor
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        if (headerIncludeController != null) {
            headerIncludeController.setGraphicController(this.graphicControllerGui);
        }

        loadUserLists();
        listView.setItems(items);
        listView.setCellFactory(param -> new CustomListCell());
    }

    @FXML
    private void initialize() {
        //no elements to initialize
    }

    private void loadUserLists() {
        items.clear();
        try {
            UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
            if (currentUser != null) {
                List<ListBean> lists = graphicControllerGui.getApplicationController().getListsForUser(currentUser);
                items.setAll(lists.stream().map(ListBean::getName).toList());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Not Logged In", "Please log in to view your lists.");
                graphicControllerGui.setScreen(SCREEN_LOGIN);
            }
        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Lists", e.getMessage());
        } catch (Exception _) {
            showAlert(Alert.AlertType.ERROR, "System Error Database", "An unexpected error occurred while loading lists.");
        }
    }

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
                refreshListView();
                showAlert(Alert.AlertType.INFORMATION, "Success", "List '" + newItemName + "' created.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error Log", "You must be logged in to create a list.");
                graphicControllerGui.setScreen(SCREEN_LOGIN);
            }
        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "List Creation Failed", e.getMessage());
        } catch (Exception _) {
            showAlert(Alert.AlertType.ERROR, "System Error Database", "An unexpected error occurred while creating the list.");
        }
    }

    private void refreshListView() {
        loadUserLists();
    }

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

        private void setupButtonActions() {
            seeButton.setOnAction(event -> navigateToScreen(getItem(), "list"));
            statsButton.setOnAction(event -> navigateToScreen(getItem(), "stats"));
            deleteButton.setOnAction(event -> handleDeleteAction(getItem()));
        }

        private void navigateToScreen(String listName, String screen) {
            if (listName == null) return;

            try {
                UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
                if (currentUser == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to view lists.");
                    graphicControllerGui.setScreen(SCREEN_LOGIN);
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
            } catch (Exception _) {
                showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during navigation.");
            }
        }

        private void handleDeleteAction(String listName) {
            if (listName == null) return;

            try {
                UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
                if (currentUser == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to delete lists.");
                    graphicControllerGui.setScreen(SCREEN_LOGIN);
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
            } catch (Exception _) {
                showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while deleting the list.");
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