package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean; // Still needed for ListModel creation
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.model.ListModel; // IMPORT YOUR LISTMODEL HERE
import ispw.project.project_ispw.model.UserModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatsController implements NavigableController, UserAwareController {

    private static final Logger LOGGER = Logger.getLogger(StatsController.class.getName());

    private static final String MINUTES = " minutes)\n";
    private static final String MINUTESP = " minutes.\n\n";
    private static final String SYSTEM_ERROR_TITLE = "System Error";
    private static final String SCREEN_LOGIN = "logIn";

    @FXML
    private TextArea statsTextArea;

    @FXML
    private Label listNameLabel;

    private GraphicControllerGui graphicControllerGui;
    private UserModel userModel;

    private ListModel selectedListModel;

    @FXML
    private HBox headerBar;
    @FXML
    private DefaultBackHomeController headerBarController;

    public StatsController() {
        // Empty constructor
    }

    @FXML
    private void initialize() {
        statsTextArea.setEditable(false);
        statsTextArea.setText("");
        listNameLabel.setText("Stats of List (Not loaded)");
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        if (headerBarController != null) {
            headerBarController.setGraphicController(this.graphicControllerGui);
        } else {
            LOGGER.log(Level.WARNING, "Header bar controller is null. The header might not function correctly.");
        }
    }

    @Override
    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;

        if (headerBarController != null) {
            headerBarController.setUserModel(this.userModel);
        }

        userModel.loggedInProperty().addListener((obs, oldVal, newVal) -> {
            LOGGER.log(Level.INFO, "StatsController Listener: loggedInProperty changed: old={0}, new={1}", new Object[]{oldVal, newVal});
            if (newVal.booleanValue()) {
                if (selectedListModel != null) {
                    LOGGER.log(Level.INFO, "StatsController Listener: User logged in, list already selected. Recalculating stats.");
                    calculateAndDisplayTotalMinutes();
                } else {
                    LOGGER.log(Level.WARNING, "StatsController Listener: User logged in, but no list selected. Redirecting to home.");
                    showAlert(Alert.AlertType.WARNING, "No List Selected", "Please select a list to view its statistics. Redirecting to home.");
                    graphicControllerGui.setScreen("home");
                }
            } else {
                LOGGER.log(Level.INFO, "StatsController Listener: User logged out. Clearing stats and redirecting.");
                statsTextArea.setText("Please log in to view statistics");
                listNameLabel.setText("Stats of List (Logged Out)");
                showAlert(Alert.AlertType.INFORMATION, "Logged Out", "You have been logged out. Statistics cleared.");
                graphicControllerGui.setScreen(SCREEN_LOGIN);
                selectedListModel = null;
            }
        });

        if (userModel.loggedInProperty().get()) {
            LOGGER.log(Level.INFO, "StatsController.setUserModel(): Initial check: User IS logged in.");
            ListBean initialSelectedListBean = graphicControllerGui.getApplicationController().getSelectedList();
            if (initialSelectedListBean != null) {
                this.selectedListModel = new ListModel(initialSelectedListBean);
                listNameLabel.textProperty().bind(selectedListModel.nameProperty());
                calculateAndDisplayTotalMinutes();
            } else {
                LOGGER.log(Level.WARNING, "StatsController.setUserModel(): No list selected initially. Redirecting to home.");
                showAlert(Alert.AlertType.WARNING, "No List Selected", "Please select a list to view its statistics. Redirecting to home.");
                graphicControllerGui.setScreen("home");
            }
        } else {
            LOGGER.log(Level.INFO, "StatsController.setUserModel(): Initial check: User IS NOT logged in. Displaying login message.");
            statsTextArea.setText("Please log in to view statistics.");
            listNameLabel.setText("Stats of List (Not Logged In)");
        }
    }

    private void calculateAndDisplayTotalMinutes() {
        if (userModel == null || !userModel.loggedInProperty().get()) {
            LOGGER.log(Level.WARNING, "calculateAndDisplayTotalMinutes: User not logged in, cannot proceed.");
            statsTextArea.setText("Please log in to view statistics.");
            showAlert(Alert.AlertType.ERROR, "Authentication Required", "You must be logged in to view statistics.");
            graphicControllerGui.setScreen(SCREEN_LOGIN);
            return;
        }
        // Use selectedListModel here, and get the underlying ListBean if needed by ApplicationController
        if (graphicControllerGui == null || selectedListModel == null) {
            LOGGER.log(Level.SEVERE, "calculateAndDisplayTotalMinutes: Initialization error. graphicControllerGui or selectedListModel is null.");
            statsTextArea.setText("Error: Application initialization issue or no list selected.");
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "Application is not initialized correctly or no list selected. Please restart or select a list.");
            return;
        }

        try {
            ListBean underlyingListBean = selectedListModel.getListBean();

            List<MovieBean> movieList = graphicControllerGui.getApplicationController().getMoviesInList(underlyingListBean);
            List<TvSeriesBean> tvSeriesList = graphicControllerGui.getApplicationController().getTvSeriesInList(underlyingListBean);
            List<AnimeBean> animeList = graphicControllerGui.getApplicationController().getAnimeInList(underlyingListBean);

            StringBuilder details = new StringBuilder();
            details.append("Details for list '").append(selectedListModel.getName()).append("':\n\n");

            int totalMinutes = 0;

            totalMinutes += appendMovieStats(movieList, details);
            totalMinutes += appendTvSeriesStats(tvSeriesList, details);
            totalMinutes += appendAnimeStats(animeList, details);

            details.append("\nOverall Total Runtime for list '").append(selectedListModel.getName()).append("': ").append(totalMinutes).append(" minutes.");
            statsTextArea.setText(details.toString());
            LOGGER.log(Level.INFO, "Stats calculated for list ''{0}''. Total minutes: {1}", new Object[]{selectedListModel.getName(), totalMinutes});

        } catch (ExceptionApplicationController e) {
            LOGGER.log(Level.SEVERE, "Error calculating stats for list ''{0}'': {1}", new Object[]{selectedListModel.getName(), e.getMessage()});
            statsTextArea.setText("Error calculating stats: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Stats Error", "Could not calculate statistics for this list: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred while calculating statistics for list ''{0}'': {1}", new Object[]{selectedListModel.getName(), e.getMessage()});
            statsTextArea.setText("An unexpected error occurred: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred while calculating statistics: " + e.getMessage());
        }
    }

    private int appendMovieStats(List<MovieBean> movieList, StringBuilder details) {
        int movieRuntime = 0;
        if (!movieList.isEmpty()) {
            details.append("--- Movies ---\n");
            for (MovieBean movie : movieList) {
                details.append("- ").append(movie.getTitle()).append(" (").append(movie.getRuntime()).append(MINUTES);
                movieRuntime += movie.getRuntime();
            }
            details.append("Total movie runtime: ").append(movieRuntime).append(MINUTESP);
        } else {
            details.append("--- No Movies in this list ---\n\n");
        }
        return movieRuntime;
    }

    private int appendTvSeriesStats(List<TvSeriesBean> tvSeriesList, StringBuilder details) {
        int tvSeriesRuntime = 0;
        if (!tvSeriesList.isEmpty()) {
            details.append("--- TV Series ---\n");
            for (TvSeriesBean tvSeries : tvSeriesList) {
                int episodeDuration = tvSeries.getEpisodeRuntime();
                int seriesTotalRuntime = episodeDuration * tvSeries.getNumberOfEpisodes();

                details.append("- ").append(tvSeries.getName()).append(" (")
                        .append(episodeDuration).append(" min/ep, ")
                        .append(tvSeries.getNumberOfEpisodes()).append(" episodes, total ")
                        .append(seriesTotalRuntime).append(MINUTES);
                tvSeriesRuntime += seriesTotalRuntime;
            }
            details.append("Total TV series runtime: ").append(tvSeriesRuntime).append(MINUTESP);
        } else {
            details.append("--- No TV Series in this list ---\n\n");
        }
        return tvSeriesRuntime;
    }

    private int appendAnimeStats(List<AnimeBean> animeList, StringBuilder details) {
        int animeRuntime = 0;
        if (!animeList.isEmpty()) {
            details.append("--- Anime ---\n");
            for (AnimeBean anime : animeList) {
                int totalAnimeRuntime = anime.getDuration() * anime.getEpisodes();

                details.append("- ").append(anime.getTitle()).append(" (")
                        .append(anime.getDuration()).append(" min/ep, ")
                        .append(anime.getEpisodes()).append(" episodes, total ")
                        .append(totalAnimeRuntime).append(MINUTES);
                animeRuntime += totalAnimeRuntime;
            }
            details.append("Total anime runtime: ").append(animeRuntime).append(MINUTESP);
        } else {
            details.append("--- No Anime in this list ---\n\n");
        }
        return animeRuntime;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}