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

/**
 * Controller for the Stats GUI screen. Displays runtime statistics for a selected list.
 * It retrieves data via the GraphicControllerGui, which delegates to the ApplicationController.
 */
public class StatsController implements NavigableController {

    private static final Logger LOGGER = Logger.getLogger(StatsController.class.getName());

    @FXML
    private TextArea statsTextArea;

    @FXML
    private Label listNameLabel; // To display the name of the list whose stats are shown

    private GraphicControllerGui graphicControllerGui;
    private ListBean selectedList; // The list for which to display stats

    /**
     * Default constructor required for JavaFX FXML loading.
     */
    public StatsController() {
        // No initialization here, dependencies are injected via setGraphicController
    }

    /**
     * Called by the GraphicControllerGui to inject necessary dependencies and initialize the view.
     * This is where the selected list is retrieved from the ApplicationController.
     *
     * @param graphicController The singleton instance of GraphicControllerGui.
     */
    @Override
    public void setGraphicController(GraphicControllerGui graphicController) {
        this.graphicControllerGui = graphicController;
        // Retrieve the selected list bean from the ApplicationController's state
        this.selectedList = graphicControllerGui.getApplicationController().getSelectedList();

        if (selectedList != null) {
            // Update the label to show the name of the currently selected list
            listNameLabel.setText(String.format("Stats for: %s", selectedList.getName()));
            calculateAndDisplayTotalMinutes();
        } else {
            // If no list is selected (e.g., direct navigation or error), log and redirect
            LOGGER.log(Level.WARNING, "No list selected for StatsController. Redirecting to home.");
            showAlert(Alert.AlertType.WARNING, "No List Selected", "Please select a list to view its statistics.");
            graphicControllerGui.setScreen("home"); // Redirect to the home screen
        }
    }

    /**
     * Initializes JavaFX components after they have been loaded from the FXML file.
     * This method is automatically called by FXMLLoader.
     */
    @FXML
    private void initialize() {
        statsTextArea.setEditable(false); // Make the TextArea read-only as it's for display
    }

    /**
     * Calculates and displays the total runtime for all movies, TV series, and anime
     * present in the {@code selectedList}.
     * All data retrieval operations are delegated to the {@code ApplicationController}.
     */
    private void calculateAndDisplayTotalMinutes() {
        // Basic check for essential dependencies before proceeding
        if (graphicControllerGui == null || selectedList == null) {
            LOGGER.log(Level.SEVERE, "Dependencies (graphicControllerGui or selectedList) not set in StatsController. Cannot calculate stats.");
            showAlert(Alert.AlertType.ERROR, "System Error", "Application is not initialized correctly. Please restart.");
            return;
        }

        try {
            // Retrieve all items for the selected list via the ApplicationController
            List<MovieBean> movieList = graphicControllerGui.getApplicationController().getMoviesInList(selectedList);
            List<TvSeriesBean> tvSeriesList = graphicControllerGui.getApplicationController().getTvSeriesInList(selectedList);
            List<AnimeBean> animeList = graphicControllerGui.getApplicationController().getAnimeInList(selectedList);

            int totalMinutes = 0;
            StringBuilder details = new StringBuilder();

            details.append("Details for list '").append(selectedList.getName()).append("':\n\n");

            // --- Process Movies ---
            if (!movieList.isEmpty()) {
                int movieRuntime = 0;
                details.append("--- Movies ---\n");
                for (MovieBean movie : movieList) {
                    details.append("- ").append(movie.getTitle()).append(" (").append(movie.getRuntime()).append(" minutes)\n");
                    movieRuntime += movie.getRuntime();
                }
                details.append("Total movie runtime: ").append(movieRuntime).append(" minutes.\n\n");
                totalMinutes += movieRuntime;
            } else {
                details.append("--- No Movies in this list ---\n\n");
            }

            // --- Process TV Series ---
            if (!tvSeriesList.isEmpty()) {
                int tvSeriesRuntime = 0;
                details.append("--- TV Series ---\n");
                for (TvSeriesBean tvSeries : tvSeriesList) {
                    // Directly use tvSeries.getEpisodeRuntime() as it's an int based on your TvSeriesBean
                    int episodeDuration = tvSeries.getEpisodeRuntime();

                    // Calculate total runtime for the specific TV series
                    int seriesTotalRuntime = episodeDuration * tvSeries.getNumberOfEpisodes();

                    details.append("- ").append(tvSeries.getName()).append(" (") // Use getName() as per your TvSeriesBean
                            .append(episodeDuration).append(" min/ep, ")
                            .append(tvSeries.getNumberOfEpisodes()).append(" episodes, total ")
                            .append(seriesTotalRuntime).append(" minutes)\n");
                    tvSeriesRuntime += seriesTotalRuntime;
                }
                details.append("Total TV series runtime: ").append(tvSeriesRuntime).append(" minutes.\n\n");
                totalMinutes += tvSeriesRuntime;
            } else {
                details.append("--- No TV Series in this list ---\n\n");
            }

            // --- Process Anime ---
            if (!animeList.isEmpty()) {
                int animeRuntime = 0;
                details.append("--- Anime ---\n");
                for (AnimeBean anime : animeList) {
                    // Calculate total runtime for the specific anime
                    int totalAnimeRuntime = anime.getDuration() * anime.getEpisodes();

                    details.append("- ").append(anime.getTitle()).append(" (")
                            .append(anime.getDuration()).append(" min/ep, ")
                            .append(anime.getEpisodes()).append(" episodes, total ")
                            .append(totalAnimeRuntime).append(" minutes)\n");
                    animeRuntime += totalAnimeRuntime;
                }
                details.append("Total anime runtime: ").append(animeRuntime).append(" minutes.\n\n");
                totalMinutes += animeRuntime;
            } else {
                details.append("--- No Anime in this list ---\n\n");
            }

            // --- Display Overall Total ---
            details.append("\nOverall Total Runtime for list '").append(selectedList.getName()).append("': ").append(totalMinutes).append(" minutes.");
            statsTextArea.setText(details.toString());
            LOGGER.log(Level.INFO, "Stats calculated for list ''{0}''. Total: {1} minutes.", new Object[]{selectedList.getName(), totalMinutes});

        } catch (ExceptionApplicationController e) {
            // Handle application-specific business logic exceptions from ApplicationController
            LOGGER.log(Level.SEVERE, "Application error calculating stats for list ''{0}'': {1}", new Object[]{selectedList.getName(), e.getMessage()});
            statsTextArea.setText("Error calculating stats: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Stats Error", "Could not calculate statistics for this list: " + e.getMessage());
        } catch (Exception e) {
            // Catch any unexpected runtime exceptions
            LOGGER.log(Level.SEVERE, "Unexpected error calculating stats for list ''{0}'': {1}");
            statsTextArea.setText("An unexpected error occurred: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while calculating statistics.");
        }
    }

    /**
     * Helper method to display a standard JavaFX Alert dialog.
     *
     * @param alertType The type of alert (e.g., INFORMATION, WARNING, ERROR).
     * @param title     The title of the alert window.
     * @param message   The main content text of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text for simplicity
        alert.setContentText(message);
        alert.showAndWait(); // Show the alert and wait for user to close it
    }
}