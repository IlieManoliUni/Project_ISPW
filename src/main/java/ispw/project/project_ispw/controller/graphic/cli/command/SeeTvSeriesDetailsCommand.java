package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;
import ispw.project.project_ispw.model.TvSeriesModel;

import java.util.List; // Needed for List
import java.util.stream.Collectors; // Needed for Collectors.joining

public class SeeTvSeriesDetailsCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser, NumberFormatException {
        if (args.isEmpty()) {
            return "Usage: seetvseriesdetails <tv_series_id>";
        }

        try {
            int tvSeriesId = Integer.parseInt(args.trim());
            TvSeriesModel tvSeries = context.getApplicationController().retrieveTvSeriesById(tvSeriesId);

            StringBuilder sb = new StringBuilder();
            sb.append("--- TV Series Details (ID: ").append(tvSeries.getId()).append(") ---\n");
            sb.append("Name: ").append(tvSeries.getName()).append("\n");
            sb.append("Original Name: ").append(tvSeries.getOriginalName()).append(" (").append(tvSeries.getOriginalLanguage()).append(")\n");
            sb.append("Overview: ").append(tvSeries.getOverview()).append("\n");
            sb.append("First Air Date: ").append(tvSeries.getFirstAirDate()).append("\n");
            sb.append("Last Air Date: ").append(tvSeries.getLastAirDate()).append("\n");
            sb.append("Number of Seasons: ").append(tvSeries.getNumberOfSeasons()).append("\n");
            sb.append("Number of Episodes: ").append(tvSeries.getNumberOfEpisodes()).append("\n");
            sb.append("Episode Runtime: ").append(tvSeries.getEpisodeRunTime()).append(" minutes\n"); // Assuming getEpisodeRunTime returns a single value or a formatted string already
            sb.append("Status: ").append(tvSeries.getStatus()).append("\n");
            sb.append("In Production: ").append(tvSeries.getInProduction() ? "Yes" : "No").append("\n");
            sb.append("Average Vote: ").append(String.format("%.2f", tvSeries.getVoteAverage())).append("\n");

            // --- CORRECTED LINES ---
            sb.append("Created By: ").append(formatTvSeriesCreators(tvSeries.getCreatedBy())).append("\n");
            sb.append("Production Companies: ").append(formatTvSeriesProductionCompanies(tvSeries.getProductionCompanies())).append("\n");
            // --- You might also want to add these for completeness if TvSeriesModel includes them ---
            sb.append("Genres: ").append(formatTvSeriesGenres(tvSeries.getGenres())).append("\n");
            sb.append("Networks: ").append(formatTvSeriesNetworks(tvSeries.getNetworks())).append("\n");
            sb.append("Spoken Languages: ").append(formatTvSeriesSpokenLanguages(tvSeries.getSpokenLanguages())).append("\n");
            sb.append("Origins: ").append(formatStringList(tvSeries.getOrigins())).append("\n"); // Assuming origins is List<String>
            // --- END CORRECTED LINES ---

            sb.append("Poster Path: ").append(tvSeries.getPosterPath() != null ? tvSeries.getPosterPath() : "N/A").append("\n");
            sb.append("--------------------------------------");

            return sb.toString();

        } catch (NumberFormatException _) {
            throw new NumberFormatException("Invalid TV Series ID. Please provide a valid integer ID.");
        } catch (ExceptionApplicationController e) {
            throw e;
        }
    }

    private String formatTvSeriesCreators(List<TvSeriesModel.Creator> creators) {
        if (creators == null || creators.isEmpty()) {
            return "N/A";
        }
        return creators.stream()
                .map(TvSeriesModel.Creator::getName)
                .collect(Collectors.joining(", "));
    }

    /**
     * Formats a list of TvSeriesModel.ProductionCompany objects into a comma-separated string of their names.
     *
     * @param companies The list of ProductionCompany objects.
     * @return A comma-separated string of company names, or "N/A" if the list is null or empty.
     */
    private String formatTvSeriesProductionCompanies(List<TvSeriesModel.ProductionCompany> companies) {
        if (companies == null || companies.isEmpty()) {
            return "N/A";
        }
        return companies.stream()
                .map(TvSeriesModel.ProductionCompany::getName)
                .collect(Collectors.joining(", "));
    }

    /**
     * Formats a list of TvSeriesModel.Genre objects into a comma-separated string of their names.
     *
     * @param genres The list of Genre objects.
     * @return A comma-separated string of genre names, or "N/A" if the list is null or empty.
     */
    private String formatTvSeriesGenres(List<TvSeriesModel.Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return "N/A";
        }
        return genres.stream()
                .map(TvSeriesModel.Genre::getName)
                .collect(Collectors.joining(", "));
    }

    /**
     * Formats a list of TvSeriesModel.Network objects into a comma-separated string of their names.
     *
     * @param networks The list of Network objects.
     * @return A comma-separated string of network names, or "N/A" if the list is null or empty.
     */
    private String formatTvSeriesNetworks(List<TvSeriesModel.Network> networks) {
        if (networks == null || networks.isEmpty()) {
            return "N/A";
        }
        return networks.stream()
                .map(TvSeriesModel.Network::getName)
                .collect(Collectors.joining(", "));
    }

    /**
     * Formats a list of TvSeriesModel.SpokenLanguage objects into a comma-separated string of their English names.
     *
     * @param languages The list of SpokenLanguage objects.
     * @return A comma-separated string of language names, or "N/A" if the list is null or empty.
     */
    private String formatTvSeriesSpokenLanguages(List<TvSeriesModel.SpokenLanguage> languages) {
        if (languages == null || languages.isEmpty()) {
            return "N/A";
        }
        return languages.stream()
                .map(TvSeriesModel.SpokenLanguage::getEnglishName) // Use getEnglishName for display
                .collect(Collectors.joining(", "));
    }

    /**
     * Formats a general list of strings into a comma-separated string.
     * Useful for fields like 'origins' if they are just List<String>.
     *
     * @param list The list of strings.
     * @return A comma-separated string, or "N/A".
     */
    private String formatStringList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "N/A";
        }
        return String.join(", ", list);
    }
}