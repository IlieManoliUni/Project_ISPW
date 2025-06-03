package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.exception.ExceptionUser;

import java.util.List;

public class SeeAllElementsListCommand implements CliCommand {

    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplication, ExceptionUser, NumberFormatException {
        if (!context.isUserLoggedIn()) {
            return "Error: You must be logged in to view list elements.";
        }

        String[] parts = args.split(" ");
        if (parts.length < 1) {
            return "Usage: seeallelementslist <listId>";
        }

        int listId = Integer.parseInt(parts[0]);

        ListBean listBean = context.getListByIdForCurrentUser(listId);

        if (listBean == null) {
            return "Error: List with ID " + listId + " not found or does not belong to you.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Elements in List '").append(listBean.getName()).append("' (ID: ").append(listId).append(") ---\n");

        try {
            List<MovieBean> movies = context.getApplicationController().getMoviesInList(listBean);
            List<TvSeriesBean> tvSeries = context.getApplicationController().getTvSeriesInList(listBean);
            List<AnimeBean> anime = context.getApplicationController().getAnimeInList(listBean);

            if (movies.isEmpty() && tvSeries.isEmpty() && anime.isEmpty()) {
                sb.append("  (This list is empty.)\n");
            } else {
                appendMovies(sb, movies);
                appendTvSeries(sb, tvSeries);
                appendAnime(sb, anime);
            }
        } catch (ExceptionApplication e) {
            return "Error retrieving list elements: " + e.getMessage();
        }

        sb.append("-------------------------------------------------------");
        return sb.toString();
    }

    private void appendMovies(StringBuilder sb, List<MovieBean> movies) {
        if (!movies.isEmpty()) {
            sb.append("  --- Movies ---\n");
            for (MovieBean movie : movies) {
                sb.append("    ID Movie: ").append(movie.getIdMovieTmdb()).append(", Title Movie: '").append(movie.getTitle()).append("'\n");
            }
        }
    }

    private void appendTvSeries(StringBuilder sb, List<TvSeriesBean> tvSeries) {
        if (!tvSeries.isEmpty()) {
            sb.append("  --- TV Series ---\n");
            for (TvSeriesBean series : tvSeries) {
                sb.append("    ID Tv Series: ").append(series.getIdTvSeriesTmdb()).append(", Title Tv Series: '").append(series.getName()).append("'\n");
            }
        }
    }

    private void appendAnime(StringBuilder sb, List<AnimeBean> anime) {
        if (!anime.isEmpty()) {
            sb.append("  --- Anime ---\n");
            for (AnimeBean a : anime) {
                sb.append("    ID Anime: ").append(a.getIdAnimeTmdb()).append(", Title Anime: '").append(a.getTitle()).append("'\n");
            }
        }
    }
}