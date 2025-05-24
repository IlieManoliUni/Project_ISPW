package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
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

public class ListController implements NavigableController {

    private static final Logger LOGGER = Logger.getLogger(ListController.class.getName());

    @FXML
    private ListView<String> listView;

    @FXML
    private Label listNameLabel;

    private final ObservableList<String> items = FXCollections.observableArrayList();
    private final Map<String, Object> itemBeanMap = new HashMap<>();

    private GraphicControllerGui graphicControllerGui;
    private ListBean selectedList;

    public ListController() {
        // Constructor is empty as dependencies are injected via setGraphicController
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;
        this.selectedList = graphicControllerGui.getApplicationController().getSelectedList();

        if (this.selectedList != null) {
            listNameLabel.setText(selectedList.getName());
            loadListItems();
        } else {
            LOGGER.log(Level.WARNING, "No list selected when ListController was initialized. Redirecting to home.");
            showAlert(Alert.AlertType.WARNING, "No List Selected", "Please select a list to view its items.");
            graphicControllerGui.setScreen("home");
        }

        listView.setItems(items);
        listView.setCellFactory(param -> new CustomListCell());
    }

    @FXML
    private void initialize() {
        // FXML initialization.
    }

    /**
     * Loads the items (movies, TV series, anime) for the currently selected list
     * by delegating to the ApplicationController.
     */
    private void loadListItems() {
        items.clear();
        itemBeanMap.clear();

        try {
            List<MovieBean> movies = graphicControllerGui.getApplicationController().getMoviesInList(selectedList);
            List<TvSeriesBean> tvSeries = graphicControllerGui.getApplicationController().getTvSeriesInList(selectedList);
            List<AnimeBean> anime = graphicControllerGui.getApplicationController().getAnimeInList(selectedList);

            for (MovieBean movie : movies) {
                String key = "Movie: " + movie.getTitle() + " (ID: " + movie.getIdMovieTmdb() + ")";
                items.add(key);
                itemBeanMap.put(key, movie);
            }

            for (TvSeriesBean series : tvSeries) {
                String key = "TV Series: " + series.getName() + " (ID: " + series.getIdTvSeriesTmdb() + ")";
                items.add(key);
                itemBeanMap.put(key, series);
            }

            for (AnimeBean a : anime) {
                String key = "Anime: " + a.getTitle() + " (ID: " + a.getIdAnimeTmdb() + ")";
                items.add(key);
                itemBeanMap.put(key, a);
            }
            LOGGER.log(Level.INFO, "Loaded items for list: {0}", selectedList.getName());

        } catch (ExceptionApplicationController e) {
            LOGGER.log(Level.SEVERE, "Application error loading list items for ''{0}'': {1}", new Object[]{selectedList.getName(), e.getMessage()});
            showAlert(Alert.AlertType.ERROR, "Error Loading List Items", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error loading list items for ''{0}''.", e);
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while loading list items.");
        }
    }

    /**
     * Inner class for custom list cell rendering, including "See" and "Delete" buttons.
     */
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

        /**
         * Handles the "See Details" action for a list item.
         * Delegates the navigation and data passing to GraphicControllerGui.
         * @param itemString The string representation of the item in the list view.
         */
        private void handleSeeAction(String itemString) {
            if (itemString == null || selectedList == null) return;

            LOGGER.log(Level.INFO, "See Details button clicked for: {0} in list {1}", new Object[]{itemString, selectedList.getName()});

            try {
                Object itemBean = itemBeanMap.get(itemString);
                if (itemBean == null) {
                    LOGGER.log(Level.WARNING, "Item not found in map for details: {0}", itemString);
                    showAlert(Alert.AlertType.ERROR, "Item Not Found", "Selected item details could not be retrieved.");
                    return;
                }

                String category;
                int id;

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
                LOGGER.log(Level.SEVERE, "Unexpected error showing item details for ''{0}''.", e);
                showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while showing details.");
            }
        }

        /**
         * Handles the "Remove" action for a list item.
         * Delegates the removal logic to the ApplicationController.
         * @param itemString The string representation of the item in the list view.
         */
        private void handleDeleteAction(String itemString) {
            if (itemString == null || selectedList == null) return;

            LOGGER.log(Level.INFO, "Remove button clicked for: {0} in list {1}", new Object[]{itemString, selectedList.getName()});

            try {
                Object itemBean = itemBeanMap.get(itemString);
                if (itemBean == null) {
                    LOGGER.log(Level.WARNING, "Item not found in map for deletion: {0}", itemString);
                    showAlert(Alert.AlertType.ERROR, "Item Not Found", "Selected item could not be removed.");
                    return;
                }

                // Pass the ID of the item instead of the full bean object
                if (itemBean instanceof MovieBean movie) {
                    graphicControllerGui.getApplicationController().removeMovieFromList(selectedList, movie.getIdMovieTmdb());
                } else if (itemBean instanceof TvSeriesBean tvSeries) {
                    graphicControllerGui.getApplicationController().removeTvSeriesFromList(selectedList, tvSeries.getIdTvSeriesTmdb());
                } else if (itemBean instanceof AnimeBean anime) {
                    graphicControllerGui.getApplicationController().removeAnimeFromList(selectedList, anime.getIdAnimeTmdb());
                } else {
                    LOGGER.log(Level.WARNING, "Unknown item type for deletion: {0}", itemBean.getClass().getName());
                    showAlert(Alert.AlertType.ERROR, "Unknown Item Type", "Cannot remove this item type.");
                    return;
                }

                // Remove from UI only after successful deletion from application layer
                getListView().getItems().remove(itemString);
                itemBeanMap.remove(itemString);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Item removed from list.");

            } catch (ExceptionApplicationController e) {
                LOGGER.log(Level.SEVERE, "Application error removing item ''{0}'' from list ''{1}'': {2}", new Object[]{itemString, selectedList.getName(), e.getMessage()});
                showAlert(Alert.AlertType.ERROR, "Removal Failed", e.getMessage());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected error removing item ''{0}'' from list ''{1}''.", new Object[]{itemString, selectedList.getName(), e});
                showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during removal.");
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