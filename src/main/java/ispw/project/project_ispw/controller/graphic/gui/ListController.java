package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.model.ListModel;
import ispw.project.project_ispw.model.UserModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.beans.value.ChangeListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListController implements NavigableController, UserAwareController {

    private static final Logger LOGGER = Logger.getLogger(ListController.class.getName());
    private static final String ID_STRING_SUFFIX = " (ID: ";
    private static final String SCREEN_LOGIN = "logIn";
    private static final String SYSTEM_ERROR_TITLE = "System Error";

    @FXML
    private ListView<String> listView;

    @FXML
    private Label listNameLabel;

    private final ObservableList<String> items = FXCollections.observableArrayList();
    private final Map<String, Object> itemBeanMap = new HashMap<>();

    private GraphicControllerGui graphicControllerGui;
    private UserModel userModel;

    private ListModel selectedListModel;

    @FXML
    private HBox headerBar;
    @FXML
    private DefaultBackHomeController headerBarController;

    private ChangeListener<Boolean> loggedInListener;

    public ListController() {
        //Default Constructor
    }

    @FXML
    private void initialize() {
        listView.setItems(items);
        listView.setCellFactory(param -> new CustomListCell());
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        if (headerBarController != null) {
            headerBarController.setGraphicController(this.graphicControllerGui);
        } else {
            LOGGER.log(Level.WARNING, "Warning: DefaultBackHomeController (headerBarController) is null in ListController. Check FXML include setup.");
        }
    }

    @Override
    public void setUserModel(UserModel userModel) {
        if (this.userModel != null && loggedInListener != null) {
            this.userModel.loggedInProperty().removeListener(loggedInListener);
        }

        this.userModel = userModel;

        if (headerBarController != null) {
            headerBarController.setUserModel(this.userModel);
        }

        if (loggedInListener == null) {
            loggedInListener = (obs, oldVal, newVal) -> {
                LOGGER.log(Level.INFO, "ListController Listener: loggedInProperty changed: old={0}, new={1}", new Object[]{oldVal, newVal});
                if (newVal.booleanValue()) {
                    handleUserLoggedIn();
                } else {
                    handleUserLoggedOut();
                }
            };
            this.userModel.loggedInProperty().addListener(loggedInListener);
        }

        if (userModel.loggedInProperty().get()) {
            LOGGER.log(Level.INFO, "ListController.setUserModel(): Initial check: User IS logged in.");
            initializeOnLogin();
        } else {
            LOGGER.log(Level.INFO, "ListController.setUserModel(): Initial check: User IS NOT logged in. Displaying login message.");
            clearAndDisplayLoggedOutState();
        }
    }

    private void handleUserLoggedIn() {
        if (selectedListModel != null) {
            LOGGER.log(Level.INFO, "ListController Listener: User logged in, list already selected. Reloading items.");
            loadListItems();
        } else {
            LOGGER.log(Level.INFO, "ListController Listener: User logged in, but no list selected initially. Attempting to get selected list.");
            ListBean currentSelectedListBean = graphicControllerGui.getApplicationController().getSelectedList();
            if (currentSelectedListBean != null) {
                initializeSelectedListModel(currentSelectedListBean);
            } else {
                showAlert(Alert.AlertType.WARNING, "None List Selected", "Please select a list to view its items. Redirecting to home.");
                graphicControllerGui.setScreen("home");
            }
        }
    }

    private void handleUserLoggedOut() {
        LOGGER.log(Level.INFO, "ListController Listener: User logged out. Clearing items.");
        items.clear();
        itemBeanMap.clear();

        if (listNameLabel.textProperty().isBound()) {
            listNameLabel.textProperty().unbind();
            LOGGER.log(Level.INFO, "ListController: Unbound listNameLabel textProperty.");
        }
        listNameLabel.setText("List Name (Logged Out)");
        selectedListModel = null;
    }

    private void initializeOnLogin() {
        ListBean initialSelectedListBean = graphicControllerGui.getApplicationController().getSelectedList();
        if (initialSelectedListBean != null) {
            initializeSelectedListModel(initialSelectedListBean);
        } else {
            LOGGER.log(Level.WARNING, "ListController.setUserModel(): No list selected initially. Redirecting to home.");
            showAlert(Alert.AlertType.WARNING, "No List Selected", "Please select a list to view its items. Redirecting to home.");
            graphicControllerGui.setScreen("home");
        }
    }

    private void clearAndDisplayLoggedOutState() {
        items.clear();
        itemBeanMap.clear();
        listNameLabel.setText("List Name (Not Logged In)");
    }

    private void initializeSelectedListModel(ListBean listBean) {
        if (selectedListModel != null && listNameLabel.textProperty().isBound()) {
            listNameLabel.textProperty().unbind();
        }
        this.selectedListModel = new ListModel(listBean);
        listNameLabel.textProperty().bind(selectedListModel.nameProperty());
        loadListItems();
        LOGGER.log(Level.INFO, "ListController: Initialized selectedListModel for list ''{0}''.", listBean.getName());
    }

    private void loadListItems() {
        items.clear();
        itemBeanMap.clear();

        if (userModel == null || !userModel.loggedInProperty().get()) {
            LOGGER.log(Level.WARNING, "loadListItems: User not logged in, cannot load items.");
            return;
        }
        if (selectedListModel == null) {
            LOGGER.log(Level.WARNING, "loadListItems: No list model selected, cannot load items.");
            return;
        }

        try {
            ListBean underlyingListBean = selectedListModel.getListBean();

            List<MovieBean> movies = graphicControllerGui.getApplicationController().getMoviesInList(underlyingListBean);
            List<TvSeriesBean> tvSeries = graphicControllerGui.getApplicationController().getTvSeriesInList(underlyingListBean);
            List<AnimeBean> anime = graphicControllerGui.getApplicationController().getAnimeInList(underlyingListBean);

            for (MovieBean movie : movies) {
                String key = "Movie: " + movie.getTitle() + ID_STRING_SUFFIX + movie.getIdMovieTmdb() + ")";
                items.add(key);
                itemBeanMap.put(key, movie);
            }

            for (TvSeriesBean series : tvSeries) {
                String key = "TV Series: " + series.getName() + ID_STRING_SUFFIX + series.getIdTvSeriesTmdb() + ")";
                items.add(key);
                itemBeanMap.put(key, series);
            }

            for (AnimeBean a : anime) {
                String key = "Anime: " + a.getTitle() + ID_STRING_SUFFIX + a.getIdAnimeTmdb() + ")";
                items.add(key);
                itemBeanMap.put(key, a);
            }

            LOGGER.log(Level.INFO, "loadListItems: Successfully loaded {0} items for list ''{1}''.", new Object[]{items.size(), selectedListModel.getName()});

        } catch (ExceptionApplication e) {
            LOGGER.log(Level.SEVERE, "Error loading list items for list ''{0}'': {1}", new Object[]{selectedListModel.getName(), e.getMessage()});
            showAlert(Alert.AlertType.ERROR, "Error Loading List Items", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred while loading list items for list ''{0}'': {1}", new Object[]{selectedListModel.getName(), e.getMessage()});
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred while loading list items: " + e.getMessage());
        }
    }

    private class CustomListCell extends ListCell<String> {
        private final HBox hbox;
        private final Text text;
        private final Button seeButton;
        private final Button deleteButton;
        private final Region spacer;

        public CustomListCell() {
            hbox = new HBox(10);
            text = new Text();
            seeButton = new Button("See Details");
            deleteButton = new Button("Remove");
            spacer = new Region();

            HBox.setHgrow(spacer, Priority.ALWAYS);
            hbox.getChildren().addAll(text, spacer, seeButton, deleteButton);

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
            seeButton.setOnAction(event -> handleSeeAction(getItem()));
            deleteButton.setOnAction(event -> handleDeleteAction(getItem()));
        }

        private void handleSeeAction(String itemString) {
            if (itemString == null || selectedListModel == null) {
                LOGGER.log(Level.WARNING, "handleSeeAction: itemString or selectedListModel is null.");
                return;
            }

            if (userModel == null || !userModel.loggedInProperty().get()) {
                LOGGER.log(Level.WARNING, "handleSeeAction: User not logged in, blocking action.");
                showAlert(Alert.AlertType.ERROR, "Authentication Required", "You must be logged in to see item details.");
                graphicControllerGui.setScreen(SCREEN_LOGIN);
                return;
            }

            try {
                Object itemBean = itemBeanMap.get(itemString);
                if (itemBean == null) {
                    LOGGER.log(Level.WARNING, "handleSeeAction: Item bean not found in map for string: {0}", itemString);
                    showAlert(Alert.AlertType.ERROR, "Item Not Found", "Selected item details could not be retrieved.");
                    return;
                }

                String category;
                int id;

                switch (itemBean) {
                    case MovieBean movie -> {
                        category = "Movie";
                        id = movie.getIdMovieTmdb();
                    }
                    case TvSeriesBean tvSeries -> {
                        category = "TvSeries";
                        id = tvSeries.getIdTvSeriesTmdb();
                    }
                    case AnimeBean anime -> {
                        category = "Anime";
                        id = anime.getIdAnimeTmdb();
                    }
                    default -> {
                        LOGGER.log(Level.WARNING, "handleSeeAction: Unknown item type for bean: {0}", itemBean.getClass().getName());
                        showAlert(Alert.AlertType.ERROR, "Unknown Item Type", "Cannot show details for this item type.");
                        return;
                    }
                }

                graphicControllerGui.navigateToItemDetails(category, id);
                LOGGER.log(Level.INFO, "Navigating to {0} details for item ''{1}'', ID {2}.", new Object[]{category, itemString, id});

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An unexpected error occurred while showing details for {0}: {1}", new Object[]{itemString, e.getMessage()});
                showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred while showing details: " + e.getMessage());
            }
        }

        private void handleDeleteAction(String itemString) {
            if (itemString == null || selectedListModel == null) {
                LOGGER.log(Level.WARNING, "handleDeleteAction: itemString or selectedListModel is null.");
                return;
            }

            if (userModel == null || !userModel.loggedInProperty().get()) {
                LOGGER.log(Level.WARNING, "handleDeleteAction: User not logged in, blocking action.");
                showAlert(Alert.AlertType.ERROR, "Authentication Required", "You must be logged in to remove items.");
                graphicControllerGui.setScreen(SCREEN_LOGIN);
                return;
            }

            try {
                Object itemBean = itemBeanMap.get(itemString);
                if (itemBean == null) {
                    LOGGER.log(Level.WARNING, "handleDeleteAction: Item bean not found in map for string: {0}", itemString);
                    showAlert(Alert.AlertType.ERROR, "Item Not Found", "Selected item could not be removed.");
                    return;
                }

                ListBean underlyingListBean = selectedListModel.getListBean();

                switch (itemBean) {
                    case MovieBean movie -> {
                        graphicControllerGui.getApplicationController().removeMovieFromList(underlyingListBean, movie.getIdMovieTmdb());
                        LOGGER.log(Level.INFO, "Removed Movie ''{0}'' from list ''{1}''.", new Object[]{itemString, selectedListModel.getName()});
                    }
                    case TvSeriesBean tvSeries -> {
                        graphicControllerGui.getApplicationController().removeTvSeriesFromList(underlyingListBean, tvSeries.getIdTvSeriesTmdb());
                        LOGGER.log(Level.INFO, "Removed TV Series ''{0}'' from list ''{1}''.", new Object[]{itemString, selectedListModel.getName()});
                    }
                    case AnimeBean anime -> {
                        graphicControllerGui.getApplicationController().removeAnimeFromList(underlyingListBean, anime.getIdAnimeTmdb());
                        LOGGER.log(Level.INFO, "Removed Anime ''{0}'' from list ''{1}''.", new Object[]{itemString, selectedListModel.getName()});
                    }
                    default -> {
                        LOGGER.log(Level.WARNING, "handleDeleteAction: Unknown item type for bean: {0}", itemBean.getClass().getName());
                        showAlert(Alert.AlertType.ERROR, "Unknown Item Type", "Cannot remove this item type.");
                        return;
                    }
                }

                getListView().getItems().remove(itemString);
                itemBeanMap.remove(itemString);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Item removed from list.");

            } catch (ExceptionApplication e) {
                LOGGER.log(Level.SEVERE, "Removal failed for item {0} from list ''{1}'': {2}", new Object[]{itemString, selectedListModel.getName(), e.getMessage()});
                showAlert(Alert.AlertType.ERROR, "Removal Failed", e.getMessage());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An unexpected error occurred during removal of item {0} from list ''{1}'': {2}", new Object[]{itemString, selectedListModel.getName(), e.getMessage()});
                showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred during removal: " + e.getMessage());
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