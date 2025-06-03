package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.exception.ExceptionUser;
import ispw.project.project_ispw.model.TvSeriesModel;

import java.util.List;
import java.util.stream.Collectors;

public class SeeTvSeriesDetailsCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplication, ExceptionUser, NumberFormatException {
        if (args.isEmpty()) {
            return "Usage: seetvseriesdetails <tv_series_id>";
        }

        try {
            int tvSeriesId = Integer.parseInt(args.trim());
            TvSeriesModel tvSeries = (TvSeriesModel) context.viewContentDetails("tvseries", tvSeriesId);

            StringBuilder sb = new StringBuilder();
            sb.append("--- TV Series Details (ID: ").append(tvSeries.getId()).append(") ---\n");
            sb.append("Name: ").append(tvSeries.getName()).append("\n");
            sb.append("Original Name: ").append(tvSeries.getOriginalName()).append(" (").append(tvSeries.getOriginalLanguage()).append(")\n");
            sb.append("Overview: ").append(tvSeries.getOverview()).append("\n");
            sb.append("First Air Date: ").append(tvSeries.getFirstAirDate()).append("\n");
            sb.append("Last Air Date: ").append(tvSeries.getLastAirDate()).append("\n");
            sb.append("Number of Seasons: ").append(tvSeries.getNumberOfSeasons()).append("\n");
            sb.append("Number of Episodes: ").append(tvSeries.getNumberOfEpisodes()).append("\n");
            sb.append("Episode Run Time: ").append(tvSeries.getEpisodeRunTime()).append(" minutes\n");
            sb.append("Average Vote: ").append(tvSeries.getVoteAverage()).append(" (Count: ").append(tvSeries.getVoteCount()).append(")\n");
            sb.append("Genres: ").append(formatTvSeriesGenres(tvSeries.getGenres())).append("\n");
            sb.append("Networks: ").append(formatTvSeriesNetworks(tvSeries.getNetworks())).append("\n");
            sb.append("Production Companies: ").append(formatTvSeriesProductionCompanies(tvSeries.getProductionCompanies())).append("\n");
            sb.append("Spoken Languages: ").append(formatTvSeriesSpokenLanguages(tvSeries.getSpokenLanguages())).append("\n");
            sb.append("Poster Path: ").append(tvSeries.getPosterPath() != null ? tvSeries.getPosterPath() : "N/A").append("\n");
            sb.append("--------------------------------------");

            return sb.toString();

        } catch (NumberFormatException _) {
            throw new NumberFormatException("Invalid TV Series ID. Please provide a valid integer ID.");
        } catch (ExceptionApplication e) {
            throw e;
        }
    }

    // --- Helper methods for formatting lists of TvSeriesModel's nested objects ---

    private String formatTvSeriesGenres(List<TvSeriesModel.Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return "N/A";
        }
        return genres.stream()
                .map(TvSeriesModel.Genre::getName)
                .collect(Collectors.joining(", "));
    }

    private String formatTvSeriesProductionCompanies(List<TvSeriesModel.ProductionCompany> companies) {
        if (companies == null || companies.isEmpty()) {
            return "N/A";
        }
        return companies.stream()
                .map(TvSeriesModel.ProductionCompany::getName)
                .collect(Collectors.joining(", "));
    }

    private String formatTvSeriesNetworks(List<TvSeriesModel.Network> networks) {
        if (networks == null || networks.isEmpty()) {
            return "N/A";
        }
        return networks.stream()
                .map(TvSeriesModel.Network::getName)
                .collect(Collectors.joining(", "));
    }

    private String formatTvSeriesSpokenLanguages(List<TvSeriesModel.SpokenLanguage> languages) {
        if (languages == null || languages.isEmpty()) {
            return "N/A";
        }
        return languages.stream()
                .map(TvSeriesModel.SpokenLanguage::getEnglishName)
                .collect(Collectors.joining(", "));
    }

}
