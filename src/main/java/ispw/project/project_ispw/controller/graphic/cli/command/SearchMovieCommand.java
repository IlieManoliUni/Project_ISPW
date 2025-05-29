package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;

import java.util.List;

public class SearchMovieCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController {
        if (args.isEmpty()) {
            return "Usage: searchmovie <query>";
        }
        List<MovieBean> results = context.getApplicationController().searchMovies(args);
        if (results.isEmpty()) {
            return "No movies found for query: '" + args + "'";
        } else {
            StringBuilder sb = new StringBuilder("Movie Search Results for '" + args + "':\n");
            for (MovieBean movie : results) {
                sb.append("  ID: ").append(movie.getIdMovieTmdb())
                        .append(", Title: '").append(movie.getTitle())
                        .append("', Runtime: ").append(movie.getRuntime()).append("\n");
            }
            return sb.toString().trim();
        }
    }
}