package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;
import ispw.project.project_ispw.model.MovieModel;

import java.util.List;
import java.util.stream.Collectors;

public class SeeMovieDetailsCommand implements CliCommand {

    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser, NumberFormatException {
        if (args.isEmpty()) {
            return "Usage: seemoviedetails <movie_id>";
        }

        try {
            int movieId = Integer.parseInt(args.trim());
            MovieModel movie = (MovieModel) context.viewContentDetails("movie", movieId);

            StringBuilder sb = new StringBuilder();
            sb.append("--- Movie Details (ID: ").append(movie.getId()).append(") ---\n");
            sb.append("Title: ").append(movie.getTitle()).append("\n");
            sb.append("Original Title: ").append(movie.getOriginalTitle()).append(" (").append(movie.getOriginalLanguage()).append(")\n");
            sb.append("Overview: ").append(movie.getOverview()).append("\n");
            sb.append("Release Date: ").append(movie.getReleaseDate()).append("\n");
            sb.append("Runtime: ").append(movie.getRuntime()).append(" minutes\n");
            sb.append("Average Vote: ").append(movie.getVoteAverage()).append(" (Count: ").append(movie.getVoteCount()).append(")\n");
            sb.append("Genres: ").append(formatMovieGenres(movie.getGenres())).append("\n");
            sb.append("Production Companies: ").append(formatMovieProductionCompanies(movie.getProductionCompanies())).append("\n");
            sb.append("Spoken Languages: ").append(formatMovieSpokenLanguages(movie.getSpokenLanguages())).append("\n");
            sb.append("Poster Path: ").append(movie.getPosterPath() != null ? movie.getPosterPath() : "N/A").append("\n");
            sb.append("--------------------------------------");

            return sb.toString();

        } catch (NumberFormatException _) {
            throw new NumberFormatException("Invalid Movie ID. Please provide a valid integer ID.");
        } catch (ExceptionApplicationController e) {
            throw e;
        }
    }

    // --- Helper methods for formatting lists of MovieModel's nested objects ---

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

    private String formatMovieSpokenLanguages(List<MovieModel.SpokenLanguage> languages) {
        if (languages == null || languages.isEmpty()) {
            return "N/A";
        }
        return languages.stream()
                .map(MovieModel.SpokenLanguage::getEnglishName)
                .collect(Collectors.joining(", "));
    }
}
