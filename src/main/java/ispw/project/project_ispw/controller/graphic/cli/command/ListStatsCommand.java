package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.exception.ExceptionUser;

import java.util.List;

public class ListStatsCommand implements CliCommand {

    private static final String MINUTES = " minutes)";
    private static final String MINUTES_PERIOD = " minutes.\n\n";

    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplication, ExceptionUser, NumberFormatException {
        if (!context.isUserLoggedIn()) {
            return "Error: You must be logged in to view list statistics.";
        }

        String[] parts = args.split(" ");
        if (parts.length < 1) {
            return "Usage: liststats <listId>";
        }

        int listId = Integer.parseInt(parts[0]);

        ListBean listBean = context.getListByIdForCurrentUser(listId);

        if (listBean == null) {
            return "Error: List with ID " + listId + " not found or does not belong to you.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Statistics for List '").append(listBean.getName()).append("' (ID: ").append(listId).append(") ---\n\n");

        int totalMinutes = 0;

        try {
            List<MovieBean> movies = context.getApplicationController().getMoviesInList(listBean);
            List<TvSeriesBean> tvSeries = context.getApplicationController().getTvSeriesInList(listBean);
            List<AnimeBean> anime = context.getApplicationController().getAnimeInList(listBean);

            totalMinutes += appendMovieStats(sb, movies);
            totalMinutes += appendTvSeriesStats(sb, tvSeries);
            totalMinutes += appendAnimeStats(sb, anime);

            sb.append("Overall Total Runtime for list '").append(listBean.getName()).append("': ").append(totalMinutes).append(" minutes.\n");

        } catch (ExceptionApplication e) {
            return "Error retrieving list statistics: " + e.getMessage();
        }

        sb.append("-------------------------------------------------------");
        return sb.toString();
    }

    private int appendMovieStats(StringBuilder sb, List<MovieBean> movieList) {
        int movieRuntime = 0;
        if (!movieList.isEmpty()) {
            sb.append("--- Movies ---\n");
            for (MovieBean movie : movieList) {
                sb.append("  - ").append(movie.getTitle()).append(" (").append(movie.getRuntime()).append(MINUTES).append("\n");
                movieRuntime += movie.getRuntime();
            }
            sb.append("  Total movie runtime: ").append(movieRuntime).append(MINUTES_PERIOD);
        } else {
            sb.append("--- No Movies in this list ---\n\n");
        }
        return movieRuntime;
    }

    private int appendTvSeriesStats(StringBuilder sb, List<TvSeriesBean> tvSeriesList) {
        int tvSeriesRuntime = 0;
        if (!tvSeriesList.isEmpty()) {
            sb.append("--- TV Series ---\n");
            for (TvSeriesBean series : tvSeriesList) {
                int episodeDuration = series.getEpisodeRuntime();
                int seriesTotalRuntime = episodeDuration * series.getNumberOfEpisodes();

                sb.append("  - ").append(series.getName()).append(" (")
                        .append(episodeDuration).append(" min/ep, ")
                        .append(series.getNumberOfEpisodes()).append(" episodes, total ")
                        .append(seriesTotalRuntime).append(MINUTES).append("\n");
                tvSeriesRuntime += seriesTotalRuntime;
            }
            sb.append("  Total TV series runtime: ").append(tvSeriesRuntime).append(MINUTES_PERIOD);
        } else {
            sb.append("--- No TV Series in this list ---\n\n");
        }
        return tvSeriesRuntime;
    }

    private int appendAnimeStats(StringBuilder sb, List<AnimeBean> animeList) {
        int animeRuntime = 0;
        if (!animeList.isEmpty()) {
            sb.append("--- Anime ---\n");
            for (AnimeBean anime : animeList) {
                int totalAnimeRuntime = anime.getDuration() * anime.getEpisodes();

                sb.append("  - ").append(anime.getTitle()).append(" (")
                        .append(anime.getDuration()).append(" min/ep, ")
                        .append(anime.getEpisodes()).append(" episodes, total ")
                        .append(totalAnimeRuntime).append(MINUTES).append("\n");
                animeRuntime += totalAnimeRuntime;
            }
            sb.append("  Total anime runtime: ").append(animeRuntime).append(MINUTES_PERIOD);
        } else {
            sb.append("--- No Anime in this list ---\n\n");
        }
        return animeRuntime;
    }
}