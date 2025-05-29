package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
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

public class SearchController implements NavigableController {

    private static final Logger LOGGER = Logger.getLogger(SearchController.class.getName());

    @FXML
    private ListView<String> listView;

    @FXML
    private Label searchResultsLabel;

    private ObservableList<String> items = FXCollections.observableArrayList();
    private GraphicControllerGui graphicControllerGui;

    private final Map<String, Object> searchResultBeanMap = new HashMap<>();

    private String currentSearchCategory;
    private String currentSearchQuery;

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

        if (currentSearchCategory != null && currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            searchResultsLabel.setText(String.format("Search Results for '%s' in %s:", currentSearchQuery, currentSearchCategory));
            performSearch();
        } else {
            showAlert(Alert.AlertType.WARNING, "No Search Performed", "Please provide a search category and query.");
            graphicControllerGui.setScreen("home");
        }

        listView.setItems(items);
        listView.setCellFactory(param -> new CustomListCell());
    }

    @FXML
    private void initialize() {
        //no elements to initialize
    }

    private void performSearch() {
        items.clear();
        searchResultBeanMap.clear();

        if (graphicControllerGui == null) {
            showAlert(Alert.AlertType.ERROR, "System Error Application", "Application setup issue. Please restart.");
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
            LOGGER.log(Level.SEVERE, () -> "An unexpected error occurred during search: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during search: " + e.getMessage());
        }

        if (items.isEmpty()) {
            items.add("No results found for '" + currentSearchQuery + "' in " + currentSearchCategory + ".");
        }
    }

    private void processSearchResults(List<?> results) {
        if (results == null || results.isEmpty()) {
            return;
        }

        for (Object bean : results) {
            String itemString;
            int itemId = 0;

            switch (bean) {
                case MovieBean movie -> {
                    itemString = "Movie: " + movie.getTitle();
                    itemId = movie.getIdMovieTmdb();
                }
                case TvSeriesBean tvSeries -> {
                    itemString = "TV Series: " + tvSeries.getName();
                    itemId = tvSeries.getIdTvSeriesTmdb();
                }
                case AnimeBean anime -> {
                    itemString = "Anime: " + anime.getTitle();
                    itemId = anime.getIdAnimeTmdb();
                }
                default -> {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING, String.format("Unexpected bean type found in search results: %s", bean.getClass().getName()));
                    }
                    continue;
                }
            }

            String key = itemString + " (ID: " + itemId + ")";
            items.add(key);
            searchResultBeanMap.put(key, bean);
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
                Object itemBean = searchResultBeanMap.get(itemString);
                if (itemBean == null) {
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
                        showAlert(Alert.AlertType.ERROR, "Unknown Item Type", "Cannot show details for this item type.");
                        return;
                    }
                }

                graphicControllerGui.navigateToItemDetails(category, id);

            } catch (ExceptionApplicationController e) {
                showAlert(Alert.AlertType.ERROR, "Error Showing Details", e.getMessage());
            } catch (Exception _) {
                showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while showing details.");
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