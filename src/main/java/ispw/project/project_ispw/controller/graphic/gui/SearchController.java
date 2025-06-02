package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.model.AnimeModel;
import ispw.project.project_ispw.model.MovieModel;
import ispw.project.project_ispw.model.TvSeriesModel;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchController implements NavigableController, UserAwareController {

    private static final Logger LOGGER = Logger.getLogger(SearchController.class.getName());
    private static final String SYSTEM_ERROR_TITLE = "System Error";
    private static final String SCREEN_LOGIN = "logIn";

    @FXML
    private ListView<String> listView;

    @FXML
    private Label searchResultsLabel;

    private ObservableList<String> items = FXCollections.observableArrayList();
    private GraphicControllerGui graphicControllerGui;
    @SuppressWarnings("squid:S1450")
    private UserModel userModel;

    private final Map<String, Object> searchResultModelMap = new HashMap<>();

    private String currentSearchCategory;
    private String currentSearchQuery;

    @FXML
    private HBox headerBar;

    @FXML
    private DefaultBackHomeController headerBarController;

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        if (headerBarController != null) {
            headerBarController.setGraphicController(this.graphicControllerGui);
        } else {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error: headerBarController is null in SearchController. Cannot set GraphicController.");
            }
        }

        this.currentSearchCategory = graphicControllerGui.getApplicationController().getSelectedSearchCategory();
        this.currentSearchQuery = graphicControllerGui.getApplicationController().getSearchQuery();

        listView.setItems(items);
        listView.setCellFactory(param -> new CustomListCell());
    }

    @Override
    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;

        if (headerBarController != null) {
            headerBarController.setUserModel(this.userModel);
        }

        userModel.loggedInProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.booleanValue()) {
                items.clear();
                searchResultModelMap.clear();
                searchResultsLabel.setText("Search Results (Logged Out)");
                showAlert(Alert.AlertType.INFORMATION, "Logged Out", "You have been logged out. Search results cleared.");
                graphicControllerGui.setScreen(SCREEN_LOGIN);
            } else {
                if (currentSearchCategory != null && currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
                    searchResultsLabel.setText(String.format("Search Results for '%s' in %s:", currentSearchQuery, currentSearchCategory));
                    performSearch();
                }
            }
        });

        if (currentSearchCategory != null && currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            searchResultsLabel.setText(String.format("Search Results for '%s' in %s:", currentSearchQuery, currentSearchCategory));
            performSearch();
        } else {
            showAlert(Alert.AlertType.WARNING, "No Search Performed", "Please provide a search category and query. Redirecting to home.");
            graphicControllerGui.setScreen("home");
        }
    }

    @FXML
    private void initialize() {
        // No elements to initialize
    }

    private void performSearch() {
        items.clear();
        searchResultModelMap.clear();

        if (graphicControllerGui == null) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "Application setup issue. Please restart.");
            return;
        }

        try {
            List<?> results = null;
            switch (currentSearchCategory) {
                case "Movie":
                    results = graphicControllerGui.getApplicationController().searchMovies(currentSearchQuery);
                    break;
                case "TvSeries":
                    results = graphicControllerGui.getApplicationController().searchTvSeries(currentSearchQuery);
                    break;
                case "Anime":
                    results = graphicControllerGui.getApplicationController().searchAnime(currentSearchQuery);
                    break;
                default:
                    throw new ExceptionApplicationController("Invalid search category: " + currentSearchCategory);
            }
            processSearchResults(results);

        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "Search Error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e, () -> "An unexpected error occurred during search: " + e.getMessage()); // Added 'e' for full stack trace
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred during search: " + e.getMessage());
        }

        if (items.isEmpty()) {
            items.add("No results found for '" + currentSearchQuery + "' in " + currentSearchCategory + ".");
        }
    }

    private void processSearchResults(List<?> results) {
        if (results == null || results.isEmpty()) {
            return;
        }

        for (Object model : results) {
            String itemString;
            int itemId = 0;

            switch (model) {
                case MovieModel movie -> {
                    itemString = "Movie: " + movie.getTitle();
                    itemId = movie.getId();
                }
                case TvSeriesModel tvSeries -> {
                    itemString = "TV Series: " + tvSeries.getName();
                    itemId = tvSeries.getId();
                }
                case AnimeModel anime -> {
                    itemString = "Anime: " + anime.getTitle();
                    itemId = anime.getId();
                }
                default -> {
                    LOGGER.log(Level.WARNING, "Unexpected model type found in search results: {0}", model.getClass().getName());
                    continue;
                }
            }

            String key = itemString + " (ID: " + itemId + ")";
            items.add(key);
            searchResultModelMap.put(key, model);
        }
    }

    private class CustomListCell extends ListCell<String> {
        private final HBox hbox;
        private final Text text;
        private final Button seeButton;
        private final Region spacer;

        public CustomListCell() {
            hbox = new HBox(10);
            text = new Text();
            seeButton = new Button("See Details");
            spacer = new Region();

            HBox.setHgrow(spacer, Priority.ALWAYS);
            hbox.getChildren().addAll(text, spacer, seeButton);

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
                seeButton.setVisible(!item.startsWith("No results found"));
            }
        }

        private void setupButtonActions() {
            seeButton.setOnAction(event -> handleSeeDetailsAction(getItem()));
        }

        private void handleSeeDetailsAction(String itemString) {
            if (itemString == null || graphicControllerGui == null) {
                return;
            }

            try {
                Object itemModel = searchResultModelMap.get(itemString);
                if (itemModel == null) {
                    showAlert(Alert.AlertType.ERROR, "Item Not Found", "Selected item details could not be retrieved.");
                    return;
                }

                String category;
                int id;

                switch (itemModel) {
                    case MovieModel movie -> {
                        category = "Movie";
                        id = movie.getId();
                    }
                    case TvSeriesModel tvSeries -> {
                        category = "TvSeries";
                        id = tvSeries.getId();
                    }
                    case AnimeModel anime -> {
                        category = "Anime";
                        id = anime.getId();
                    }
                    default -> {
                        showAlert(Alert.AlertType.ERROR, "Unknown Item Type", "Cannot show details for this item type.");
                        return;
                    }
                }

                graphicControllerGui.navigateToItemDetails(category, id);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e, () -> "An unexpected error occurred while showing details: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred while showing details: " + e.getMessage());
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