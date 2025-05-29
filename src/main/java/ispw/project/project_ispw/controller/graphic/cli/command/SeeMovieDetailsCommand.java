package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

public class SeeMovieDetailsCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser, NumberFormatException {
        if (args.isEmpty()) {
            return "Usage: seemoviedetails <movie_id>";
        }

        try {
            int movieId = Integer.parseInt(args.trim());
            MovieBean movie = context.getApplicationController().retrieveMovieById(movieId);

            StringBuilder sb = new StringBuilder();
            sb.append("--- Movie Details (ID: ").append(movie.getIdMovieTmdb()).append(") ---\n");
            sb.append("Title: ").append(movie.getTitle()).append("\n");
            sb.append("Original Title: ").append(movie.getOriginalTitle()).append(" (").append(movie.getOriginalLanguage()).append(")\n");
            sb.append("Overview: ").append(movie.getOverview()).append("\n");
            sb.append("Release Date: ").append(movie.getReleaseDate()).append("\n");
            sb.append("Runtime: ").append(movie.getRuntime()).append(" minutes\n");
            sb.append("Genres: ").append(String.join(", ", movie.getGenres())).append("\n");
            sb.append("Average Vote: ").append(String.format("%.2f", movie.getVoteAverage())).append("\n");
            sb.append("Budget: $").append(movie.getBudget()).append("\n");
            sb.append("Revenue: $").append(movie.getRevenue()).append("\n");
            sb.append("Production Companies: ").append(String.join(", ", movie.getProductionCompanies())).append("\n");
            sb.append("Poster Path: ").append(movie.getPosterPath() != null ? movie.getPosterPath() : "N/A").append("\n");
            sb.append("--------------------------------------");

            return sb.toString();

        } catch (NumberFormatException _) {
            throw new NumberFormatException("Invalid movie ID. Please provide a valid integer ID.");
        } catch (ExceptionApplicationController e) {
            throw e;
        }
    }
}