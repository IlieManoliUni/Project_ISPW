package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.model.AnimeModel;
import ispw.project.project_ispw.model.MovieModel;
import ispw.project.project_ispw.model.TvSeriesModel;
import ispw.project.project_ispw.model.UserModel;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox; // Keep this import for the addToListContainer and the header HBox

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ShowController implements NavigableController, UserAwareController {

    private static final Logger LOGGER = Logger.getLogger(ShowController.class.getName());
    private static final String TITLE = "Title: ";
    private static final String MIN = " min\n";
    private static final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private static final String SYSTEM_ERROR_TITLE = "System Error";
    private static final String SCREEN_LOGIN = "logIn";

    @FXML
    private ImageView photoView;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField listNameField;

    @FXML
    private Button addToListButton;

    @FXML
    private HBox addToListContainer;

    private GraphicControllerGui graphicControllerGui;
    private UserModel userModel;

    private String currentCategory;
    private int currentId;
    private Object currentItemBean;

    @FXML
    private HBox headerBar;

    @FXML
    private DefaultBackHomeController headerBarController;

    public ShowController() {
        //empty constructor
    }

    @FXML
    private void initialize() {
        descriptionArea.setWrapText(true);
        if (addToListContainer != null) {
            addToListContainer.setVisible(false);
            addToListContainer.setManaged(false);
        }
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        if (headerBarController != null) {
            headerBarController.setGraphicController(this.graphicControllerGui);
        } else {
            LOGGER.log(Level.WARNING, "Header bar controller is null. The header might not function correctly.");
        }

        this.currentCategory = graphicControllerGui.getApplicationController().getSelectedItemCategory();
        this.currentId = graphicControllerGui.getApplicationController().getSelectedItemId();
    }

    @Override
    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;

        if (headerBarController != null) {
            headerBarController.setUserModel(this.userModel);
        }

        userModel.loggedInProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.booleanValue()) {
                toggleAddToListControls(true);
            } else {
                toggleAddToListControls(false);
                showAlert(Alert.AlertType.INFORMATION, "Logged Out", "You have been logged out. Cannot add items to list.");
            }
        });

        toggleAddToListControls(userModel.loggedInProperty().get());

        if (currentCategory != null && currentId != 0) {
            displayDetails();
        } else {
            showAlert(Alert.AlertType.WARNING, "No Item Selected", "Please select an item to view its details. Redirecting to home.");
            graphicControllerGui.setScreen("home");
        }
    }

    private void toggleAddToListControls(boolean visible) {
        if (addToListContainer != null) {
            addToListContainer.setVisible(visible);
            addToListContainer.setManaged(visible);
        }
    }

    private void displayDetails() {
        if (graphicControllerGui == null) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "Application setup issue. Please restart.");
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
                graphicControllerGui.setScreen("home");
                return;
            }

            populateDetailsInUI();

        } catch (ExceptionApplicationController e) {
            showAlert(Alert.AlertType.ERROR, "Display Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred while displaying details: " + e.getMessage());
        }
    }

    private void populateDetailsInUI() {
        StringBuilder detailsText = new StringBuilder();
        String imageUrl = null;

        switch (currentItemBean) {
            case MovieModel movie -> {
                detailsText.append(TITLE).append(movie.getTitle()).append("\n");
                detailsText.append("Overview: ").append(movie.getOverview()).append("\n\n");
                detailsText.append("Original Title: ").append(movie.getOriginalTitle()).append("\n");
                detailsText.append("Original Language: ").append(movie.getOriginalLanguage()).append("\n");
                detailsText.append("Release Date: ").append(movie.getReleaseDate()).append("\n");
                detailsText.append("Runtime: ").append(movie.getRuntime()).append(MIN);
                detailsText.append("Genres: ").append(formatMovieGenres(movie.getGenres())).append("\n");
                detailsText.append("Vote Average: ").append(String.format("%.1f", movie.getVoteAverage())).append("\n");
                detailsText.append("Budget: $").append(movie.getBudget()).append("\n");
                detailsText.append("Revenue: $").append(movie.getRevenue()).append("\n");
                detailsText.append("Production Companies: ").append(formatMovieProductionCompanies(movie.getProductionCompanies())).append("\n");
                imageUrl = movie.getPosterPath();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    imageUrl = TMDB_IMAGE_BASE_URL + imageUrl;
                }
            }
            case TvSeriesModel tvSeries -> {
                detailsText.append(TITLE).append(tvSeries.getName()).append("\n");
                detailsText.append("Overview: ").append(tvSeries.getOverview()).append("\n\n");
                detailsText.append("Original Name: ").append(tvSeries.getOriginalName()).append("\n");
                detailsText.append("Original Language: ").append(tvSeries.getOriginalLanguage()).append("\n");
                detailsText.append("First Air Date: ").append(tvSeries.getFirstAirDate()).append("\n");
                detailsText.append("Last Air Date: ").append(tvSeries.getLastAirDate()).append("\n");
                detailsText.append("Number of Seasons: ").append(tvSeries.getNumberOfSeasons()).append("\n");
                detailsText.append("Number of Episodes: ").append(tvSeries.getNumberOfEpisodes()).append("\n");
                detailsText.append("Episode Run Time: ").append(tvSeries.getEpisodeRunTime()).append(MIN);
                detailsText.append("In Production: ").append(tvSeries.getInProduction()).append("\n");
                detailsText.append("Status: ").append(tvSeries.getStatus()).append("\n");
                detailsText.append("Vote Average: ").append(String.format("%.1f", tvSeries.getVoteAverage())).append("\n");
                detailsText.append("Created By: ").append(formatTvSeriesCreators(tvSeries.getCreatedBy())).append("\n");
                detailsText.append("Production Companies: ").append(formatTvSeriesProductionCompanies(tvSeries.getProductionCompanies())).append("\n");
                imageUrl = tvSeries.getPosterPath();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    imageUrl = TMDB_IMAGE_BASE_URL + imageUrl;
                }
            }
            case AnimeModel anime -> {
                detailsText.append(TITLE).append(anime.getTitle()).append("\n");
                detailsText.append("Description: ").append(anime.getDescription()).append("\n\n");
                detailsText.append("Episodes: ").append(anime.getEpisodes()).append("\n");
                detailsText.append("Duration: ").append(anime.getDuration()).append(MIN);
                detailsText.append("Genres: ").append(formatStringList(anime.getGenres())).append("\n");
                detailsText.append("Country of Origin: ").append(anime.getCountryOfOrigin()).append("\n");
                detailsText.append("Start Date: ").append(anime.getStartDate()).append("\n");
                detailsText.append("End Date: ").append(anime.getEndDate()).append("\n");
                detailsText.append("Average Score: ").append(anime.getAverageScore()).append("\n");
                detailsText.append("Mean Score: ").append(anime.getMeanScore()).append("\n");
                detailsText.append("Status: ").append(anime.getStatus()).append("\n");
                detailsText.append("Next Airing Episode: ").append(anime.getNextAiringEpisode()).append("\n");
                if (anime.getCoverImage() != null) {
                    imageUrl = anime.getCoverImage().getMedium();
                } else {
                    imageUrl = null;
                }
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
            } catch (IllegalArgumentException e) {
                String finalImageUrl = imageUrl;
                LOGGER.log(Level.WARNING, e, () -> "Failed to load image from URL: " + finalImageUrl);
                photoView.setImage(null);
            }
        } else {
            photoView.setImage(null);
        }
    }

    private String formatStringList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "N/A";
        }
        return String.join(", ", list);
    }

    private String formatMovieGenres(List<MovieModel.Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return "N/A";
        }
        return genres.stream()
                .map(MovieModel.Genre::getName)
                .collect(Collectors.joining(", "));
    }

    private String formatMovieProductionCompanies(List<MovieModel.ProductionCompany> companies) {
        if (companies == null || companies.isEmpty()) {
            return "N/A";
        }
        return companies.stream()
                .map(MovieModel.ProductionCompany::getName)
                .collect(Collectors.joining(", "));
    }

    private String formatTvSeriesProductionCompanies(List<TvSeriesModel.ProductionCompany> companies) {
        if (companies == null || companies.isEmpty()) {
            return "N/A";
        }
        return companies.stream()
                .map(TvSeriesModel.ProductionCompany::getName)
                .collect(Collectors.joining(", "));
    }

    private String formatTvSeriesCreators(List<TvSeriesModel.Creator> creators) {
        if (creators == null || creators.isEmpty()) {
            return "N/A";
        }
        return creators.stream()
                .map(TvSeriesModel.Creator::getName)
                .collect(Collectors.joining(", "));
    }



    @FXML
    private void addToUserList() {
        if (graphicControllerGui == null) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "Application is not initialized correctly.");
            return;
        }

        if (userModel == null || !userModel.loggedInProperty().get()) {
            showAlert(Alert.AlertType.INFORMATION, "Not Logged In", "Please log in to add items to a list.");
            graphicControllerGui.setScreen(SCREEN_LOGIN);
            return;
        }

        try {
            UserBean currentUser = userModel.currentUserProperty().get();
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "User data not available. Please log in again.");
                graphicControllerGui.setScreen(SCREEN_LOGIN);
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
                showAlert(Alert.AlertType.ERROR, "List Not Found", "List '" + listName + "' not found for current user. Please create it first.");
                return;
            }

            boolean success = false;
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
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred while adding to list: " + e.getMessage());
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