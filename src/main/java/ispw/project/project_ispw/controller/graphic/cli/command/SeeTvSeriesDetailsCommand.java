package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

public class SeeTvSeriesDetailsCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser, NumberFormatException {
        if (args.isEmpty()) {
            return "Usage: seetvseriesdetails <tv_series_id>";
        }

        try {
            int tvSeriesId = Integer.parseInt(args.trim());
            TvSeriesBean tvSeries = context.getApplicationController().retrieveTvSeriesById(tvSeriesId);

            StringBuilder sb = new StringBuilder();
            sb.append("--- TV Series Details (ID: ").append(tvSeries.getIdTvSeriesTmdb()).append(") ---\n");
            sb.append("Name: ").append(tvSeries.getName()).append("\n");
            sb.append("Original Name: ").append(tvSeries.getOriginalName()).append(" (").append(tvSeries.getOriginalLanguage()).append(")\n");
            sb.append("Overview: ").append(tvSeries.getOverview()).append("\n");
            sb.append("First Air Date: ").append(tvSeries.getFirstAirDate()).append("\n");
            sb.append("Last Air Date: ").append(tvSeries.getLastAirDate()).append("\n");
            sb.append("Number of Seasons: ").append(tvSeries.getNumberOfSeasons()).append("\n");
            sb.append("Number of Episodes: ").append(tvSeries.getNumberOfEpisodes()).append("\n");
            sb.append("Episode Runtime: ").append(tvSeries.getEpisodeRuntime()).append(" minutes\n");
            sb.append("Status: ").append(tvSeries.getStatus()).append("\n");
            sb.append("In Production: ").append(tvSeries.isInProduction() ? "Yes" : "No").append("\n");
            sb.append("Average Vote: ").append(String.format("%.2f", tvSeries.getVoteAverage())).append("\n");
            sb.append("Created By: ").append(String.join(", ", tvSeries.getCreatedBy())).append("\n");
            sb.append("Production Companies: ").append(String.join(", ", tvSeries.getProductionCompanies())).append("\n");
            sb.append("Poster Path: ").append(tvSeries.getPosterPath() != null ? tvSeries.getPosterPath() : "N/A").append("\n");
            sb.append("--------------------------------------");

            return sb.toString();

        } catch (NumberFormatException _) {
            throw new NumberFormatException("Invalid TV Series ID. Please provide a valid integer ID.");
        } catch (ExceptionApplicationController e) {
            throw e;
        }
    }
}