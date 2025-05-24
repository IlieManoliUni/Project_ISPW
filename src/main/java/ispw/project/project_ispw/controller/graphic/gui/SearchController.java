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

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        this.currentSearchCategory = graphicControllerGui.getApplicationController().getSelectedSearchCategory();
        this.currentSearchQuery = graphicControllerGui.getApplicationController().getSearchQuery();

        if (currentSearchCategory != null && currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            searchResultsLabel.setText(String.format("Search Results for '%s' in %s:", currentSearchQuery, currentSearchCategory));
            performSearch();
        } else {
            LOGGER.log(Level.WARNING, "No search category or query found. Redirecting to home.");
            showAlert(Alert.AlertType.WARNING, "No Search Performed", "Please provide a search category and query.");
            graphicControllerGui.setScreen("home");
        }

        listView.setItems(items);
        listView.setCellFactory(param -> new CustomListCell());
    }

    @FXML
    private void initialize() {
        // Initialization logic, if any.
    }

    /**
     * Performs the search based on the category and search text stored in ApplicationController.
     * Updates the ListView with the results.
     */
    private void performSearch() {
        items.clear();
        searchResultBeanMap.clear();

        if (graphicControllerGui == null) {
            LOGGER.log(Level.SEVERE, "GraphicControllerGui not injected in SearchController.");
            showAlert(Alert.AlertType.ERROR, "System Error", "Application setup issue. Please restart.");
            return;
        }

        try {
            // Call the general searchContent method from ApplicationController
            List<?> results = graphicControllerGui.getApplicationController().searchContent(currentSearchCategory, currentSearchQuery);
            processSearchResults(results); // Process the list of beans directly

            LOGGER.log(Level.INFO, "Search completed for category: {0}, query: {1}", new Object[]{currentSearchCategory, currentSearchQuery});

        } catch (ExceptionApplicationController e) {
            LOGGER.log(Level.SEVERE, "Application error during search for {0} - {1}: {2}", new Object[]{currentSearchCategory, currentSearchQuery, e.getMessage()});
            showAlert(Alert.AlertType.ERROR, "Search Error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during search for {0} - {1}: {2}", new Object[]{currentSearchCategory, currentSearchQuery, e});
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during search.");
        }

        if (items.isEmpty()) {
            items.add("No results found for '" + currentSearchQuery + "' in " + currentSearchCategory + ".");
        }
    }

    /**
     * Processes the list of search results (beans) and populates the ListView.
     * @param results The list of Bean objects (MovieBean, TvSeriesBean, AnimeBean).
     */
    private void processSearchResults(List<?> results) {
        if (results == null || results.isEmpty()) {
            LOGGER.log(Level.INFO, "No results to process.");
            return;
        }

        for (Object bean : results) {
            String itemString;
            int itemId = 0;

            if (bean instanceof MovieBean movie) {
                itemString = "Movie: " + movie.getTitle();
                itemId = movie.getIdMovieTmdb();
            } else if (bean instanceof TvSeriesBean tvSeries) {
                itemString = "TV Series: " + tvSeries.getName();
                itemId = tvSeries.getIdTvSeriesTmdb();
            } else if (bean instanceof AnimeBean anime) {
                itemString = "Anime: " + anime.getTitle();
                itemId = anime.getIdAnimeTmdb();
            } else {
                LOGGER.log(Level.WARNING, "Unknown bean type encountered: {0}", bean.getClass().getName());
                continue;
            }

            String key = itemString + " (ID: " + itemId + ")";
            items.add(key);
            searchResultBeanMap.put(key, bean);
        }
    }

    /**
     * Custom ListCell class with "See Details" button.
     */
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

        /**
         * Handles the "See Details" action for a list item.
         * Delegates the navigation and data passing to GraphicControllerGui.
         * @param itemString The string representation of the item in the list view.
         */
        private void handleSeeDetailsAction(String itemString) {
            if (itemString == null || graphicControllerGui == null) {
                LOGGER.log(Level.WARNING, "Cannot show details: itemString or graphicControllerGui is null.");
                return;
            }

            LOGGER.log(Level.INFO, "See Details button clicked for: {0}", itemString);

            try {
                Object itemBean = searchResultBeanMap.get(itemString);
                if (itemBean == null) {
                    LOGGER.log(Level.WARNING, "Item not found in map for details: {0}", itemString);
                    showAlert(Alert.AlertType.ERROR, "Item Not Found", "Selected item details could not be retrieved.");
                    return;
                }

                String category = "";
                int id = 0;

                if (itemBean instanceof MovieBean movie) {
                    category = "Movie";
                    id = movie.getIdMovieTmdb();
                } else if (itemBean instanceof TvSeriesBean tvSeries) {
                    category = "TV Series";
                    id = tvSeries.getIdTvSeriesTmdb();
                } else if (itemBean instanceof AnimeBean anime) {
                    category = "Anime";
                    id = anime.getIdAnimeTmdb();
                } else {
                    LOGGER.log(Level.WARNING, "Unknown item type for details: {0}", itemBean.getClass().getName());
                    showAlert(Alert.AlertType.ERROR, "Unknown Item Type", "Cannot show details for this item type.");
                    return;
                }

                graphicControllerGui.navigateToItemDetails(category, id);

            } catch (ExceptionApplicationController e) {
                LOGGER.log(Level.SEVERE, "Application error showing item details for ''{0}'': {1}", new Object[]{itemString, e.getMessage()});
                showAlert(Alert.AlertType.ERROR, "Error Showing Details", e.getMessage());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected error showing item details for ''{0}''.");
                showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while showing details.");
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