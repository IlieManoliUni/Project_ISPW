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

public class ListController implements NavigableController {

    private static final String ID_STRING = " (ID: ";

    @FXML
    private ListView<String> listView;

    @FXML
    private Label listNameLabel;

    private final ObservableList<String> items = FXCollections.observableArrayList();
    private final Map<String, Object> itemBeanMap = new HashMap<>();

    private GraphicControllerGui graphicControllerGui;
    private ListBean selectedList;

    @FXML
    private DefaultBackHomeController headerBarController;

    public ListController() {
        //Empty constructor
    }

    @FXML
    private void initialize() {
        //no elements
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        if (headerBarController != null) {
            headerBarController.setGraphicController(this.graphicControllerGui);
        } else {
            // Consider logging this or providing a more visible alert if the header bar is critical
        }

        this.selectedList = graphicControllerGui.getApplicationController().getSelectedList();

        if (this.selectedList != null) {
            listNameLabel.setText(selectedList.getName());
            loadListItems();
        } else {
            showAlert(Alert.AlertType.WARNING, "No List Selected", "Please select a list to view its items.");
            graphicControllerGui.setScreen("home");
        }

        listView.setItems(items);
        listView.setCellFactory(param -> new CustomListCell());
    }

    private void loadListItems() {
        items.clear();
        itemBeanMap.clear();

        try {
            List<MovieBean> movies = graphicControllerGui.getApplicationController().getMoviesInList(selectedList);
            List<TvSeriesBean> tvSeries = graphicControllerGui.getApplicationController().getTvSeriesInList(selectedList);
            List<AnimeBean> anime = graphicControllerGui.getApplicationController().getAnimeInList(selectedList);

            for (MovieBean movie : movies) {
                String key = "Movie: " + movie.getTitle() + ID_STRING + movie.getIdMovieTmdb() + ")";
                items.add(key);
                itemBeanMap.put(key, movie);
            }

            for (TvSeriesBean series : tvSeries) {
                String key = "TV Series: " + series.getName() + ID_STRING + series.getIdTvSeriesTmdb() + ")";
                items.add(key);
                itemBeanMap.put(key, series);
            }

            for (AnimeBean a : anime) {
                String key = "Anime: " + a.getTitle() + ID_STRING + a.getIdAnimeTmdb() + ")";
                items.add(key);
                itemBeanMap.put(key, a);
            }

        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading List Items", e.getMessage());
        } catch (Exception _) {
            showAlert(Alert.AlertType.ERROR, "System Error Database", "An unexpected error occurred while loading list items.");
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
            if (itemString == null || selectedList == null) return;

            try {
                Object itemBean = itemBeanMap.get(itemString);
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

        private void handleDeleteAction(String itemString) {
            if (itemString == null || selectedList == null) return;

            try {
                Object itemBean = itemBeanMap.get(itemString);
                if (itemBean == null) {
                    showAlert(Alert.AlertType.ERROR, "Item Not Found", "Selected item could not be removed.");
                    return;
                }

                switch (itemBean) {
                    case MovieBean movie -> graphicControllerGui.getApplicationController().removeMovieFromList(selectedList, movie.getIdMovieTmdb());
                    case TvSeriesBean tvSeries -> graphicControllerGui.getApplicationController().removeTvSeriesFromList(selectedList, tvSeries.getIdTvSeriesTmdb());
                    case AnimeBean anime -> graphicControllerGui.getApplicationController().removeAnimeFromList(selectedList, anime.getIdAnimeTmdb());
                    default -> {
                        showAlert(Alert.AlertType.ERROR, "Unknown Item Type", "Cannot remove this item type.");
                        return;
                    }
                }

                getListView().getItems().remove(itemString);
                itemBeanMap.remove(itemString);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Item removed from list.");

            } catch (ExceptionApplicationController e) {
                showAlert(Alert.AlertType.ERROR, "Removal Failed", e.getMessage());
            } catch (Exception _) {
                showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during removal.");
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