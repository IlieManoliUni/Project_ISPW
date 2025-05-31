package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.model.MovieModel;

import java.util.List;

public class SearchMovieCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController {
        if (args.isEmpty()) {
            return "Usage: searchmovie <query>";
        }
        List<MovieModel> results = context.getApplicationController().searchMovies(args);
        if (results.isEmpty()) {
            return "No movies found for query: '" + args + "'";
        } else {
            StringBuilder sb = new StringBuilder("Movie Search Results for '" + args + "':\n");
            for (MovieModel movie : results) {
                sb.append("  ID: ").append(movie.getId())
                        .append(", Title: '").append(movie.getTitle())
                        .append("', Runtime: ").append(movie.getRuntime()).append("\n");
            }
            return sb.toString().trim();
        }
    }
}