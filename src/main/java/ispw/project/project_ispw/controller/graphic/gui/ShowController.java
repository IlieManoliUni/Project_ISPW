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

    private static final Logger LOGGER = Logger.getLogger(ShowController.class.getName());
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

    public ShowController() {
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;
        this.currentCategory = graphicControllerGui.getApplicationController().getSelectedItemCategory();
        this.currentId = graphicControllerGui.getApplicationController().getSelectedItemId();

        if (currentCategory != null && currentId != 0) {
            displayDetails();
        } else {
            LOGGER.log(Level.WARNING, "No item selected for ShowController. Redirecting to home.");
            showAlert(Alert.AlertType.WARNING, "No Item Selected", "Please select an item to view its details.");
            graphicControllerGui.setScreen("home");
        }
    }

    @FXML
    private void initialize() {
        descriptionArea.setWrapText(true);
        addToListButton.setOnAction(event -> addToUserList());
    }

    private void displayDetails() {
        if (graphicControllerGui == null) {
            LOGGER.log(Level.SEVERE, "GraphicControllerGui not injected in ShowController.");
            showAlert(Alert.AlertType.ERROR, "System Error", "Application setup issue. Please restart.");
            return;
        }

        try {
            switch (currentCategory) {
                case "Movie":
                    this.currentItemBean = graphicControllerGui.getApplicationController().retrieveMovieById(currentId);
                    break;
                case "TV Series":
                    this.currentItemBean = graphicControllerGui.getApplicationController().retrieveTvSeriesById(currentId);
                    break;
                case "Anime":
                    this.currentItemBean = graphicControllerGui.getApplicationController().retrieveAnimeById(currentId);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Invalid category for display: {0}", currentCategory);
                    showAlert(Alert.AlertType.ERROR, "Invalid Category", "Cannot display details for this type.");
                    return;
            }

            if (currentItemBean == null) {
                LOGGER.log(Level.WARNING, "Item with ID {0} not found for category {1}.", new Object[]{currentId, currentCategory});
                showAlert(Alert.AlertType.ERROR, "Item Not Found", "Details for the selected item could not be retrieved.");
                return;
            }

            populateDetailsInUI();
            LOGGER.log(Level.INFO, "Displayed details for {0}: {1} (ID: {2})", new Object[]{currentCategory, getTitleFromBean(currentItemBean), currentId});

        } catch (ExceptionApplicationController e) {
            LOGGER.log(Level.SEVERE, "Application error displaying details for {0} (ID: {1}): {2}", new Object[]{currentCategory, currentId, e.getMessage()});
            showAlert(Alert.AlertType.ERROR, "Display Error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error displaying details for {0} (ID: {1}): {2}", new Object[]{currentCategory, currentId, e.getMessage()});
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while displaying details.");
        }
    }

    private void populateDetailsInUI() {
        StringBuilder detailsText = new StringBuilder();
        String imageUrl = null;
        String fullImageUrl = null;

        if (currentItemBean instanceof MovieBean movie) {
            detailsText.append("Title: ").append(movie.getTitle()).append("\n");
            detailsText.append("Overview: ").append(movie.getOverview()).append("\n\n");
            detailsText.append("Original Title: ").append(movie.getOriginalTitle()).append("\n");
            detailsText.append("Original Language: ").append(movie.getOriginalLanguage()).append("\n");
            detailsText.append("Release Date: ").append(movie.getReleaseDate()).append("\n");
            detailsText.append("Runtime: ").append(movie.getRuntime()).append(" min\n");
            if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
                detailsText.append("Genres: ").append(String.join(", ", movie.getGenres())).append("\n");
            } else {
                detailsText.append("Genres: N/A\n");
            }
            detailsText.append("Vote Average: ").append(String.format("%.1f", movie.getVoteAverage())).append("\n");
            detailsText.append("Budget: $").append(movie.getBudget()).append("\n");
            detailsText.append("Revenue: $").append(movie.getRevenue()).append("\n");
            if (movie.getProductionCompanies() != null && !movie.getProductionCompanies().isEmpty()) {
                detailsText.append("Production Companies: ").append(String.join(", ", movie.getProductionCompanies())).append("\n");
            } else {
                detailsText.append("Production Companies: N/A\n");
            }
            imageUrl = movie.getPosterPath();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                fullImageUrl = TMDB_IMAGE_BASE_URL + imageUrl;
            }
        } else if (currentItemBean instanceof TvSeriesBean tvSeries) {
            detailsText.append("Title: ").append(tvSeries.getName()).append("\n");
            detailsText.append("Overview: ").append(tvSeries.getOverview()).append("\n\n");
            detailsText.append("Original Name: ").append(tvSeries.getOriginalName()).append("\n");
            detailsText.append("Original Language: ").append(tvSeries.getOriginalLanguage()).append("\n");
            detailsText.append("First Air Date: ").append(tvSeries.getFirstAirDate()).append("\n");
            detailsText.append("Last Air Date: ").append(tvSeries.getLastAirDate()).append("\n");
            detailsText.append("Number of Seasons: ").append(tvSeries.getNumberOfSeasons()).append("\n");
            detailsText.append("Number of Episodes: ").append(tvSeries.getNumberOfEpisodes()).append("\n");
            detailsText.append("Episode Run Time: ").append(tvSeries.getEpisodeRuntime()).append(" min\n");
            detailsText.append("In Production: ").append(tvSeries.isInProduction()).append("\n");
            detailsText.append("Status: ").append(tvSeries.getStatus()).append("\n");
            detailsText.append("Vote Average: ").append(String.format("%.1f", tvSeries.getVoteAverage())).append("\n");
            if (tvSeries.getCreatedBy() != null && !tvSeries.getCreatedBy().isEmpty()) {
                detailsText.append("Created By: ").append(String.join(", ", tvSeries.getCreatedBy())).append("\n");
            } else {
                detailsText.append("Created By: N/A\n");
            }
            if (tvSeries.getProductionCompanies() != null && !tvSeries.getProductionCompanies().isEmpty()) {
                detailsText.append("Production Companies: ").append(String.join(", ", tvSeries.getProductionCompanies())).append("\n");
            } else {
                detailsText.append("Production Companies: N/A\n");
            }
            imageUrl = tvSeries.getPosterPath();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                fullImageUrl = TMDB_IMAGE_BASE_URL + imageUrl;
            }
        } else if (currentItemBean instanceof AnimeBean anime) {
            detailsText.append("Title: ").append(anime.getTitle()).append("\n");
            detailsText.append("Description: ").append(anime.getDescription()).append("\n\n");
            detailsText.append("Episodes: ").append(anime.getEpisodes()).append("\n");
            detailsText.append("Duration: ").append(anime.getDuration()).append(" min\n");
            if (anime.getGenres() != null && !anime.getGenres().isEmpty()) {
                detailsText.append("Genres: ").append(String.join(", ", anime.getGenres())).append("\n");
            } else {
                detailsText.append("Genres: N/A\n");
            }
            detailsText.append("Country of Origin: ").append(anime.getCountryOfOrigin()).append("\n");
            detailsText.append("Start Date: ").append(anime.getStartDate()).append("\n");
            detailsText.append("End Date: ").append(anime.getEndDate()).append("\n");
            detailsText.append("Average Score: ").append(anime.getAverageScore()).append("\n");
            detailsText.append("Mean Score: ").append(anime.getMeanScore()).append("\n");
            detailsText.append("Status: ").append(anime.getStatus()).append("\n");
            detailsText.append("Next Airing Episode: ").append(anime.getNextAiringEpisodeDetails()).append("\n");
            imageUrl = anime.getCoverImageUrl();
            fullImageUrl = imageUrl;
        }

        descriptionArea.setText(detailsText.toString());

        if (fullImageUrl != null && !fullImageUrl.isEmpty()) {
            try {
                Image image = new Image(fullImageUrl);
                photoView.setImage(image);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Invalid image URL: {0}", fullImageUrl);
                photoView.setImage(null);
            }
        } else {
            photoView.setImage(null);
        }
    }

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
            if (currentItemBean instanceof MovieBean movie) {
                // Corrected: Pass the movie ID (getIdMovieTmdb()) as per ApplicationController's signature
                success = graphicControllerGui.getApplicationController().addMovieToList(targetList, movie.getIdMovieTmdb());
            } else if (currentItemBean instanceof TvSeriesBean tvSeries) {
                // Corrected: Pass the TV Series ID (getIdTvSeriesTmdb()) as per ApplicationController's signature
                success = graphicControllerGui.getApplicationController().addTvSeriesToList(targetList, tvSeries.getIdTvSeriesTmdb());
            } else if (currentItemBean instanceof AnimeBean anime) {
                // Corrected: Pass the anime ID (getIdAnimeTmdb()) as per ApplicationController's signature
                success = graphicControllerGui.getApplicationController().addAnimeToList(targetList, anime.getIdAnimeTmdb());
            } else {
                showAlert(Alert.AlertType.ERROR, "Unknown Item Type", "Cannot add this type of item to a list.");
                return;
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", currentCategory + " added to list '" + listName + "'.");
                listNameField.clear();
                LOGGER.log(Level.INFO, "{0} (ID: {1}) added to list ''{2}'' by user ''{3}''.",
                        new Object[]{currentCategory, currentId, listName, currentUser.getUsername()});
            } else {
                showAlert(Alert.AlertType.WARNING, "Add Failed", "Could not add " + currentCategory + " to list. It might already be there.");
            }

        } catch (ExceptionApplicationController e) {
            LOGGER.log(Level.SEVERE, "Application error adding item to list: {0}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error Adding to List", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error adding item to list.", e);
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while adding to list.");
        }
    }

    private String getTitleFromBean(Object bean) {
        if (bean instanceof MovieBean movie) return movie.getTitle();
        if (bean instanceof TvSeriesBean tvSeries) return tvSeries.getName();
        if (bean instanceof AnimeBean anime) return anime.getTitle();
        return "Unknown Title";
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}