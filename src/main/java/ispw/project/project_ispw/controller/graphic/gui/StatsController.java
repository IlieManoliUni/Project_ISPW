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

/**
 * Controller for the Stats GUI screen. Displays runtime statistics for a selected list.
 * It retrieves data via the GraphicControllerGui, which delegates to the ApplicationController.
 */
public class StatsController implements NavigableController {

    @FXML
    private TextArea statsTextArea;

    @FXML
    private Label listNameLabel;

    private GraphicControllerGui graphicControllerGui;
    private ListBean selectedList;

    // --- NEW: FXML injection for the included DefaultBackHomeController ---
    // This field will be automatically populated by FXMLLoader
    // if 'stats.fxml' has <fx:include fx:id="headerBar" .../>
    @FXML
    private DefaultBackHomeController headerBarController; // Assuming fx:id="headerBar" in stats.fxml
    // --- END NEW ---

    /**
     * Default constructor required for JavaFX FXML loading.
     */
    public StatsController() {
        // No initialization here, dependencies are injected via setGraphicController
    }

    /**
     * Initializes JavaFX components after they have been loaded from the FXML file.
     * This method is automatically called by FXMLLoader.
     */
    @FXML
    private void initialize() {
        // This method runs BEFORE setGraphicController.
        statsTextArea.setEditable(false);
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

        // --- NEW: Manually inject GraphicControllerGui into the DefaultBackHomeController ---
        // This is crucial because DefaultBackHomeController is embedded via fx:include
        if (headerBarController != null) {
            headerBarController.setGraphicController(this.graphicControllerGui);
        } else {
            // Consider more robust error handling or a visual cue if headerBarController is null
        }
        // --- END NEW ---

        // Retrieve the selected list bean from the ApplicationController's state
        this.selectedList = graphicControllerGui.getApplicationController().getSelectedList();

        if (selectedList != null) {
            // Update the label to show the name of the currently selected list
            listNameLabel.setText(String.format("Stats for: %s", selectedList.getName()));
            calculateAndDisplayTotalMinutes();
        } else {
            // If no list is selected (e.g., direct navigation or error), log and redirect
            showAlert(Alert.AlertType.WARNING, "No List Selected", "Please select a list to view its statistics.");
            graphicControllerGui.setScreen("home"); // Redirect to the home screen
        }
    }

    /**
     * Calculates and displays the total runtime for all movies, TV series, and anime
     * present in the {@code selectedList}.
     * All data retrieval operations are delegated to the {@code ApplicationController}.
     */
    private void calculateAndDisplayTotalMinutes() {
        // Basic check for essential dependencies before proceeding
        if (graphicControllerGui == null || selectedList == null) {
            statsTextArea.setText("Error: Application initialization issue."); // Update UI to reflect error
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
                    int episodeDuration = tvSeries.getEpisodeRuntime();
                    int seriesTotalRuntime = episodeDuration * tvSeries.getNumberOfEpisodes();

                    details.append("- ").append(tvSeries.getName()).append(" (")
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

        } catch (ExceptionApplicationController e) {
            statsTextArea.setText("Error calculating stats: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Stats Error", "Could not calculate statistics for this list: " + e.getMessage());
        } catch (Exception e) {
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
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}