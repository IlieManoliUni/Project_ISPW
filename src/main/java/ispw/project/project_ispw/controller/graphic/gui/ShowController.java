package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShowController implements NavigableController {

    private static final String TITLE = "Title: ";
    private static final String MIN = " min\n";


    private static final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    @FXML
    private ImageView photoView;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField listNameField;

    @FXML
    private Button addToListButton;

    private GraphicControllerGui graphicControllerGui;

    private String currentCategory;
    private int currentId;
    private Object currentItemBean;

    @FXML
    private DefaultBackHomeController headerBarController;

    public ShowController() {
        //empty constructor
    }

    @FXML
    private void initialize() {
        descriptionArea.setWrapText(true);
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        if (headerBarController != null) {
            headerBarController.setGraphicController(this.graphicControllerGui);
        } else {
            Logger.getLogger(ShowController.class.getName()).log(Level.WARNING, "Header bar controller is null. The header might not function correctly.");
        }

        this.currentCategory = graphicControllerGui.getApplicationController().getSelectedItemCategory();
        this.currentId = graphicControllerGui.getApplicationController().getSelectedItemId();

        if (currentCategory != null && currentId != 0) {
            displayDetails();
        } else {
            showAlert(Alert.AlertType.WARNING, "No Item Selected", "Please select an item to view its details.");
            graphicControllerGui.setScreen("home");
        }
    }

    private void displayDetails() {
        if (graphicControllerGui == null) {
            showAlert(Alert.AlertType.ERROR, "System Error Application", "Application setup issue. Please restart.");
            return;
        }

        try {
            switch (currentCategory) {
                case "Movie":
                    this.currentItemBean = graphicControllerGui.getApplicationController().retrieveMovieById(currentId);
                    break;
                case "TvSeries":
                    this.currentItemBean = graphicControllerGui.getApplicationController().retrieveTvSeriesById(currentId);
                    break;
                case "Anime":
                    this.currentItemBean = graphicControllerGui.getApplicationController().retrieveAnimeById(currentId);
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Invalid Category", "Cannot display details for this type.");
                    return;
            }

            if (currentItemBean == null) {
                showAlert(Alert.AlertType.ERROR, "Item Not Found", "Details for the selected item could not be retrieved.");
                return;
            }

            populateDetailsInUI();

        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "Display Error", e.getMessage());
        } catch (Exception _) {
            showAlert(Alert.AlertType.ERROR, "System Error Display", "An unexpected error occurred while displaying details.");
        }
    }

    private void populateDetailsInUI() {
        StringBuilder detailsText = new StringBuilder();
        String imageUrl = null;

        switch (currentItemBean) {
            case MovieBean movie -> {
                detailsText.append(TITLE).append(movie.getTitle()).append("\n");
                detailsText.append("Overview: ").append(movie.getOverview()).append("\n\n");
                detailsText.append("Original Title: ").append(movie.getOriginalTitle()).append("\n");
                detailsText.append("Original Language: ").append(movie.getOriginalLanguage()).append("\n");
                detailsText.append("Release Date: ").append(movie.getReleaseDate()).append("\n");
                detailsText.append("Runtime: ").append(movie.getRuntime()).append(MIN);
                detailsText.append("Genres: ").append(formatList(movie.getGenres())).append("\n");
                detailsText.append("Vote Average: ").append(String.format("%.1f", movie.getVoteAverage())).append("\n");
                detailsText.append("Budget: $").append(movie.getBudget()).append("\n");
                detailsText.append("Revenue: $").append(movie.getRevenue()).append("\n");
                detailsText.append("Production Companies: ").append(formatList(movie.getProductionCompanies())).append("\n");
                imageUrl = movie.getPosterPath();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    imageUrl = TMDB_IMAGE_BASE_URL + imageUrl;
                }
            }
            case TvSeriesBean tvSeries -> {
                detailsText.append(TITLE).append(tvSeries.getName()).append("\n");
                detailsText.append("Overview: ").append(tvSeries.getOverview()).append("\n\n");
                detailsText.append("Original Name: ").append(tvSeries.getOriginalName()).append("\n");
                detailsText.append("Original Language: ").append(tvSeries.getOriginalLanguage()).append("\n");
                detailsText.append("First Air Date: ").append(tvSeries.getFirstAirDate()).append("\n");
                detailsText.append("Last Air Date: ").append(tvSeries.getLastAirDate()).append("\n");
                detailsText.append("Number of Seasons: ").append(tvSeries.getNumberOfSeasons()).append("\n");
                detailsText.append("Number of Episodes: ").append(tvSeries.getNumberOfEpisodes()).append("\n");
                detailsText.append("Episode Run Time: ").append(tvSeries.getEpisodeRuntime()).append(MIN);
                detailsText.append("In Production: ").append(tvSeries.isInProduction()).append("\n");
                detailsText.append("Status: ").append(tvSeries.getStatus()).append("\n");
                detailsText.append("Vote Average: ").append(String.format("%.1f", tvSeries.getVoteAverage())).append("\n");
                detailsText.append("Created By: ").append(formatList(tvSeries.getCreatedBy())).append("\n");
                detailsText.append("Production Companies: ").append(formatList(tvSeries.getProductionCompanies())).append("\n");
                imageUrl = tvSeries.getPosterPath();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    imageUrl = TMDB_IMAGE_BASE_URL + imageUrl;
                }
            }
            case AnimeBean anime -> {
                detailsText.append(TITLE).append(anime.getTitle()).append("\n");
                detailsText.append("Description: ").append(anime.getDescription()).append("\n\n");
                detailsText.append("Episodes: ").append(anime.getEpisodes()).append("\n");
                detailsText.append("Duration: ").append(anime.getDuration()).append(MIN);
                detailsText.append("Genres: ").append(formatList(anime.getGenres())).append("\n");
                detailsText.append("Country of Origin: ").append(anime.getCountryOfOrigin()).append("\n");
                detailsText.append("Start Date: ").append(anime.getStartDate()).append("\n");
                detailsText.append("End Date: ").append(anime.getEndDate()).append("\n");
                detailsText.append("Average Score: ").append(anime.getAverageScore()).append("\n");
                detailsText.append("Mean Score: ").append(anime.getMeanScore()).append("\n");
                detailsText.append("Status: ").append(anime.getStatus()).append("\n");
                detailsText.append("Next Airing Episode: ").append(anime.getNextAiringEpisodeDetails()).append("\n");
                imageUrl = anime.getCoverImageUrl();
            }
            default -> {
                showAlert(Alert.AlertType.ERROR, "Unknown Item Type", "Cannot display details for this item type.");
                return;
            }
        }

        descriptionArea.setText(detailsText.toString());

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image image = new Image(imageUrl);
                photoView.setImage(image);
            } catch (IllegalArgumentException _) {
                photoView.setImage(null);
            }
        } else {
            photoView.setImage(null);
        }
    }

    private String formatList(List<String> list) {
        return (list != null && !list.isEmpty()) ? String.join(", ", list) : "N/A";
    }


    @FXML
    private void addToUserList() {
        if (graphicControllerGui == null) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Application is not initialized correctly.");
            return;
        }

        try {
            UserBean currentUser = graphicControllerGui.getApplicationController().getCurrentUserBean();
            if (currentUser == null) {
                showAlert(Alert.AlertType.INFORMATION, "Not Logged In", "Please log in to add items to a list.");
                return;
            }

            String listName = listNameField.getText().trim();
            if (listName.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a list name.");
                return;
            }

            ListBean targetList = graphicControllerGui.getApplicationController().findListForUserByName(
                    currentUser, listName
            );

            if (targetList == null) {
                showAlert(Alert.AlertType.ERROR, "List Not Found", "List '" + listName + "' not found. Please create it first.");
                return;
            }

            boolean success = false;
            // Replaced if-else if chain with switch expression
            switch (currentItemBean) {
                case MovieBean movie ->
                        success = graphicControllerGui.getApplicationController().addMovieToList(targetList, movie.getIdMovieTmdb());
                case TvSeriesBean tvSeries ->
                        success = graphicControllerGui.getApplicationController().addTvSeriesToList(targetList, tvSeries.getIdTvSeriesTmdb());
                case AnimeBean anime ->
                        success = graphicControllerGui.getApplicationController().addAnimeToList(targetList, anime.getIdAnimeTmdb());
                default -> {
                    showAlert(Alert.AlertType.ERROR, "Unknown Item Type", "Cannot add this type of item to a list.");
                    return;
                }
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", currentCategory + " added to list '" + listName + "'.");
                listNameField.clear();
            } else {
                showAlert(Alert.AlertType.WARNING, "Add Failed", "Could not add " + currentCategory + " to list. It might already be there.");
            }

        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "Error Adding to List", e.getMessage());
        } catch (Exception _) {
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while adding to list.");
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