package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;
import ispw.project.project_ispw.model.MovieModel;

import java.util.List; // Needed for List
import java.util.stream.Collectors; // Needed for Collectors.joining

public class SeeMovieDetailsCommand implements CliCommand {

    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser, NumberFormatException {
        if (args.isEmpty()) {
            return "Usage: seemoviedetails <movie_id>";
        }

        try {
            int movieId = Integer.parseInt(args.trim());
            MovieModel movie = context.getApplicationController().retrieveMovieById(movieId);

            StringBuilder sb = new StringBuilder();
            sb.append("--- Movie Details (ID: ").append(movie.getId()).append(") ---\n");
            sb.append("Title: ").append(movie.getTitle()).append("\n");
            sb.append("Original Title: ").append(movie.getOriginalTitle()).append(" (").append(movie.getOriginalLanguage()).append(")\n");
            sb.append("Overview: ").append(movie.getOverview()).append("\n");
            sb.append("Release Date: ").append(movie.getReleaseDate()).append("\n");
            sb.append("Runtime: ").append(movie.getRuntime()).append(" minutes\n");

            // --- CORRECTED LINES ---
            sb.append("Genres: ").append(formatMovieGenres(movie.getGenres())).append("\n");
            sb.append("Average Vote: ").append(String.format("%.2f", movie.getVoteAverage())).append("\n");
            sb.append("Budget: $").append(movie.getBudget()).append("\n");
            sb.append("Revenue: $").append(movie.getRevenue()).append("\n");
            sb.append("Production Companies: ").append(formatMovieProductionCompanies(movie.getProductionCompanies())).append("\n");
            // --- END CORRECTED LINES ---

            sb.append("Poster Path: ").append(movie.getPosterPath() != null ? movie.getPosterPath() : "N/A").append("\n");
            sb.append("--------------------------------------");

            return sb.toString();

        } catch (NumberFormatException _) {
            throw new NumberFormatException("Invalid movie ID. Please provide a valid integer ID.");
        } catch (ExceptionApplicationController e) {
            throw e;
        }
    }

    // --- Helper methods for formatting lists of MovieModel's nested objects ---

    /**
     * Formats a list of MovieModel.Genre objects into a comma-separated string of their names.
     *
     * @param genres The list of Genre objects.
     * @return A comma-separated string of genre names, or "N/A" if the list is null or empty.
     */
    private String formatMovieGenres(List<MovieModel.Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return "N/A";
        }
        return genres.stream()
                .map(MovieModel.Genre::getName) // Extract the name from each Genre object
                .collect(Collectors.joining(", ")); // Join the names with ", "
    }

    /**
     * Formats a list of MovieModel.ProductionCompany objects into a comma-separated string of their names.
     *
     * @param companies The list of ProductionCompany objects.
     * @return A comma-separated string of company names, or "N/A" if the list is null or empty.
     */
    private String formatMovieProductionCompanies(List<MovieModel.ProductionCompany> companies) {
        if (companies == null || companies.isEmpty()) {
            return "N/A";
        }
        return companies.stream()
                .map(MovieModel.ProductionCompany::getName) // Extract the name from each ProductionCompany object
                .collect(Collectors.joining(", ")); // Join the names with ", "
    }
}