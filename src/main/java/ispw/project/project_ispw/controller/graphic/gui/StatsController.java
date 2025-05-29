package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatsController implements NavigableController {

    private static final Logger LOGGER = Logger.getLogger(StatsController.class.getName());

    private static final String MINUTES = " minutes)\n";
    private static final String MINUTESP = " minutes.\n\n";

    @FXML
    private TextArea statsTextArea;

    @FXML
    private Label listNameLabel;

    private GraphicControllerGui graphicControllerGui;
    private ListBean selectedList;

    @FXML
    private DefaultBackHomeController headerBarController;

    public StatsController() {
        //Empty constructor
    }

    @FXML
    private void initialize() {
        statsTextArea.setEditable(false);
    }

    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;

        if (headerBarController != null) {
            headerBarController.setGraphicController(this.graphicControllerGui);
        } else {
            LOGGER.log(Level.WARNING, "Header bar controller is null. The header might not function correctly.");
        }

        this.selectedList = graphicControllerGui.getApplicationController().getSelectedList();

        if (selectedList != null) {
            listNameLabel.setText(String.format("Stats for: %s", selectedList.getName()));
            calculateAndDisplayTotalMinutes();
        } else {
            showAlert(Alert.AlertType.WARNING, "No List Selected", "Please select a list to view its statistics.");
            graphicControllerGui.setScreen("home");
        }
    }

    private void calculateAndDisplayTotalMinutes() {
        if (graphicControllerGui == null || selectedList == null) {
            statsTextArea.setText("Error: Application initialization issue.");
            showAlert(Alert.AlertType.ERROR, "System Error", "Application is not initialized correctly. Please restart.");
            return;
        }

        try {
            List<MovieBean> movieList = graphicControllerGui.getApplicationController().getMoviesInList(selectedList);
            List<TvSeriesBean> tvSeriesList = graphicControllerGui.getApplicationController().getTvSeriesInList(selectedList);
            List<AnimeBean> animeList = graphicControllerGui.getApplicationController().getAnimeInList(selectedList);

            StringBuilder details = new StringBuilder();
            details.append("Details for list '").append(selectedList.getName()).append("':\n\n");

            int totalMinutes = 0;

            totalMinutes += appendMovieStats(movieList, details);
            totalMinutes += appendTvSeriesStats(tvSeriesList, details);
            totalMinutes += appendAnimeStats(animeList, details);

            details.append("\nOverall Total Runtime for list '").append(selectedList.getName()).append("': ").append(totalMinutes).append(" minutes.");
            statsTextArea.setText(details.toString());

        } catch (ExceptionApplicationController e) {
            statsTextArea.setText("Error calculating stats: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Stats Error", "Could not calculate statistics for this list: " + e.getMessage());
        } catch (Exception e) {
            statsTextArea.setText("An unexpected error occurred: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while calculating statistics.");
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